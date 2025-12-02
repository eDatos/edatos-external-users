package es.gobcan.istac.edatos.external.users.core.errors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import es.gobcan.istac.edatos.external.users.core.i18n.LocaleContext;
import org.apache.commons.collections.CollectionUtils;
import org.siemac.edatos.core.common.exception.EDatosException;
import org.siemac.edatos.core.common.exception.EDatosExceptionItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller advice to translate the server side exceptions to client-friendly
 * json structures.
 */
@ControllerAdvice
public class ExceptionTranslator {

    private final Logger log = LoggerFactory.getLogger(ExceptionTranslator.class);

    private final MessageSource messageSource;

    public ExceptionTranslator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ConcurrencyFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorVM processConcurrencyError(ConcurrencyFailureException ex) {
        return new ErrorVM(ErrorConstants.ERR_CONCURRENCY_FAILURE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorVM processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        ErrorVM dto = new ErrorVM(ErrorConstants.ERR_VALIDATION);
        for (FieldError fieldError : fieldErrors) {
            dto.add(fieldError.getObjectName(), fieldError.getField(), fieldError.getCode());
        }
        return dto;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ParameterizedErrorVM processParameterizedValidationError(ConstraintViolationException ex) {
        return new CustomParameterizedExceptionBuilder().message("La entidad no se ha podido almancenar")
                .errorItems(ex.getConstraintViolations().stream().map(this::getParameterizedItemFromViolation).collect(Collectors.toList())).cause(ex).code(ErrorConstants.ERR_DATA_CONSTRAINT).build()
                .getParameterizedErrorVM();
    }

    private ParameterizedErrorItem getParameterizedItemFromViolation(ConstraintViolation<?> violation) {
        // @formatter:off
        return new ParameterizedErrorItemBuilder().message("'" + violation.getPropertyPath().toString() + "' " + violation.getMessage())
                                                  .code(ErrorConstants.ERR_DATA_CONSTRAINT)
                                                  .build();
        // @formatter:on
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorVM processParameterizedValidationError(DataIntegrityViolationException ex) {
        return new ErrorVM(ErrorConstants.ERR_DATA_CONSTRAINT, "La entidad no se ha podido almancenar");
    }

    @ExceptionHandler(CustomParameterizedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ParameterizedErrorVM processParameterizedValidationError(CustomParameterizedException ex) {
        return Boolean.TRUE.equals(ex.getTranslate()) ? ex.getParameterizedErrorVM(messageSource) : ex.getParameterizedErrorVM();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void processAccessDeniedException(AccessDeniedException e) {
        // The issue https://github.com/spring-projects/spring-security/issues/6908 explains why this handler is necessary
        throw e;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorVM processMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        return new ErrorVM(ErrorConstants.ERR_METHOD_NOT_SUPPORTED, exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorVM> processException(Exception ex) {
        log.debug("Error inesperado: {}", ex.getMessage(), ex);
        BodyBuilder builder;
        ErrorVM errorVM;
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            builder = ResponseEntity.status(responseStatus.value());
            errorVM = new ErrorVM("error." + responseStatus.value().value(), responseStatus.reason());
        } else {
            builder = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
            errorVM = new ErrorVM(ErrorConstants.ERR_INTERNAL_SERVER_ERROR, "Internal server error");
        }
        return builder.body(errorVM);
    }

    @ExceptionHandler(EDatosException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ParameterizedErrorVM processParameterizedValidationError(EDatosException eDatosException) {
        List<ParameterizedErrorItem> items = new ArrayList<>();
        List<EDatosExceptionItem> exceptionItems = eDatosException.getExceptionItems();
        EDatosExceptionItem principalException = CollectionUtils.isNotEmpty(exceptionItems) ? exceptionItems.get(exceptionItems.size() - 1) : null;
        exceptionItems.remove(principalException);
        for (EDatosExceptionItem item : exceptionItems) {
            items.add(toParameterizedErrorItem(item));
        }
        if (principalException != null) {
            List<String> params = principalException.getMessageParameters() != null
                    ? Stream.of(principalException.getMessageParameters()).map(Object::toString).collect(Collectors.toList())
                    : new ArrayList<>();

            return new ParameterizedErrorVM(principalException.getMessage(), principalException.getCode(), params, items);
        } else {
            return new ParameterizedErrorVM(items);
        }
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ErrorVM processMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        return new ErrorVM(ErrorConstants.ERR_VALIDATION, "Parámetro requerido ausente: " + exception.getParameterName());
    }

    @ExceptionHandler(CaptchaUnsuccessfulException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ParameterizedErrorVM processCaptchaClientError(CaptchaUnsuccessfulException captchaUnsuccessfulException) {
        return new ParameterizedErrorVM(ServiceExceptionType.CAPTCHA_UNSUCCESSFUL.getMessageForReasonType(), ServiceExceptionType.CAPTCHA_UNSUCCESSFUL.getCode(), null, null);
    }

    @ExceptionHandler(InternalCaptchaException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ParameterizedErrorVM processCaptchaClientError(InternalCaptchaException internalCaptchaException) {
        return new ParameterizedErrorVM(ServiceExceptionType.GENERIC_ERROR.getMessageForReasonType(), ServiceExceptionType.GENERIC_ERROR.getCode(), null, null);
    }

    @ExceptionHandler(JpaOptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ParameterizedErrorVM optimistickLockingError(JpaOptimisticLockingFailureException ex) {
        return new ParameterizedErrorVM(ServiceExceptionType.OPTIMISTIC_LOCKING.getMessageForReasonType(LocaleContext.getLocale()), ServiceExceptionType.OPTIMISTIC_LOCKING.getCode(), null,
                null);
    }

    private static ParameterizedErrorItem toParameterizedErrorItem(EDatosExceptionItem exceptionItem) {
        List<String> messageParameters = exceptionItem.getMessageParameters() != null
                ? Stream.of(exceptionItem.getMessageParameters()).map(Object::toString).collect(Collectors.toList())
                : new ArrayList<>();
        String[] params = messageParameters.toArray(new String[messageParameters.size()]);

        ParameterizedErrorItem parameterizedErrorItem = new ParameterizedErrorItem(exceptionItem.getMessage(), exceptionItem.getCode(), params);

        for (EDatosExceptionItem item : exceptionItem.getExceptionItems()) {
            parameterizedErrorItem.addErrorItem(toParameterizedErrorItem(item));
        }
        return parameterizedErrorItem;
    }
}
