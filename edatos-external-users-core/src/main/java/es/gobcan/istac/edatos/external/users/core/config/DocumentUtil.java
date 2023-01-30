package es.gobcan.istac.edatos.external.users.core.config;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import es.gobcan.istac.edatos.external.users.core.errors.CustomParameterizedExceptionBuilder;
import es.gobcan.istac.edatos.external.users.core.errors.ErrorConstants;

public final class DocumentUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentUtil.class);

    private DocumentUtil() {}

    public static void download(byte[] document, String name, HttpServletResponse response) {
        try (OutputStream os = response.getOutputStream()) {
            response.setContentLength(document.length);
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", name));
            StreamUtils.copy(document, os);
        } catch (IOException e) {
            LOGGER.error("Exception obtaining the file.", e);
            throw new CustomParameterizedExceptionBuilder().message(String.format("Exception obtaining file")).code(ErrorConstants.FILE_NOT_FOUND).build();
        }
    }
}
