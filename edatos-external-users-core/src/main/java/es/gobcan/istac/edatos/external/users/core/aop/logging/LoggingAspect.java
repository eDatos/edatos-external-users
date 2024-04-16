package es.gobcan.istac.edatos.external.users.core.aop.logging;

import java.util.Arrays;
import java.util.List;

import es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.siemac.edatos.core.common.exception.EDatosException;
import org.siemac.edatos.core.common.exception.EDatosExceptionItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aspect for logging execution of service and repository Spring components.
 */
@Aspect
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     */
    // @formatter:off
    @Pointcut("within(@org.springframework.stereotype.Repository *)"
        + " || within(@org.springframework.stereotype.Service *)"
        + " || within(@org.springframework.web.bind.annotation.RestController *)")
    // @formatter:on
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the
        // advices.
    }

    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void springServiceAndRestControllerBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut("within(es.gobcan.istac.edatos.external.users..*)")
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Advice that logs methods throwing exceptions.
     *
     * @param joinPoint
     *            join point for advice
     * @param e
     *            exception
     */
    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        List<String> codesToWarnAndHideStackTrace = Arrays.asList(ServiceExceptionType.LOGIN_FAILED.getCode());

        String message = "Exception in {}.{}() with cause = '{}' and exception = '{}'";
        Object cause = e.getCause() != null ? e.getCause() : "NULL";
        EDatosExceptionItem firstExceptionItem = null;
        if (e instanceof EDatosException && ((EDatosException) e).getExceptionItems().size() > 0) {
            firstExceptionItem = ((EDatosException) e).getExceptionItems().get(0);
        }

        if (firstExceptionItem != null && codesToWarnAndHideStackTrace.contains(firstExceptionItem.getCode())) {
            log.warn(message, joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), cause, e.getMessage());
        } else {
            log.error(message, joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), cause, e.getMessage(), e);
        }
    }

    /**
     * Advice that logs when a method is entered and exited.
     *
     * @param joinPoint
     *            join point for advice
     * @return result
     * @throws Throwable
     *             throws IllegalArgumentException
     */
    @Around("applicationPackagePointcut() && springServiceAndRestControllerBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("Enter: {}.{}() with argument[s] = {}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        }
        try {
            Object result = joinPoint.proceed();
            if (log.isDebugEnabled()) {
                log.debug("Exit: {}.{}() with result = {}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), result);
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()), joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());

            throw e;
        }
    }
}
