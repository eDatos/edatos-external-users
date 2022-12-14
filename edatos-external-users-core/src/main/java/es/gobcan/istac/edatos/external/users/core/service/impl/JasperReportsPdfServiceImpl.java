package es.gobcan.istac.edatos.external.users.core.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.siemac.edatos.core.common.exception.EDatosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.gobcan.istac.edatos.external.users.core.config.DocumentConstants;
import es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType;
import es.gobcan.istac.edatos.external.users.core.service.adapter.JasperReportsAdapter;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

@Service("jasperReportsPdfService")
public class JasperReportsPdfServiceImpl implements JasperReportsAdapter {
    private static final String ERROR_WHILE_GENERATING_JASPER_REPORT_MESSAGE = "Error while generating Jasper Report";
    private static final Logger LOGGER = LoggerFactory.getLogger(JasperReportsPdfServiceImpl.class);

    @Override
    public byte[] generateFromTemplate(String templateFile, Map<String, Object> templateParams) {

        String path = DocumentConstants.FOLDER_JASPER_REPORT + "external-user-information.jrxml";
        InputStream resourceAsStream = getClass().getResourceAsStream(path);
        InputStream resourceAsStream3 = getClass().getResourceAsStream("reports/" + "external-user-information.jasper");
        InputStream resourceAsStream4 = getClass().getResourceAsStream("reports/" + "external-user-information.jrxml");

        try (InputStream resourceAsStream2 = getClass().getResourceAsStream(path)) {
            OutputStream outputStream = new ByteArrayOutputStream();

            JasperReport compileReport = (JasperReport) JRLoader.loadObject(resourceAsStream2);

            Map<String, Object> params = new HashMap<>();
            if (templateParams != null) {
                params.putAll(templateParams);
            }

            JasperPrint fillReport = JasperFillManager.fillReport(compileReport, params, new JREmptyDataSource());
            JRPdfExporter exporter = new JRPdfExporter();
            ExporterInput exportedInput = new SimpleExporterInput(fillReport);
            OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(outputStream);
            exporter.setExporterInput(exportedInput);
            exporter.setExporterOutput(exporterOutput);
            exporter.exportReport();

            return ((ByteArrayOutputStream) outputStream).toByteArray();
        } catch (JRException | IOException e) {
            LOGGER.error(ERROR_WHILE_GENERATING_JASPER_REPORT_MESSAGE, e);
            throw new EDatosException(ServiceExceptionType.ERROR_JASPER_REPORTS_GENERATING);
        }
    }

}
