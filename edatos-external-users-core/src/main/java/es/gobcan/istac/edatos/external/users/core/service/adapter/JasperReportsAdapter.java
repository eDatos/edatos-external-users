package es.gobcan.istac.edatos.external.users.core.service.adapter;

import java.util.Map;

public interface JasperReportsAdapter {

    byte[] generateFromTemplate(String templateFile, Map<String, Object> params);
}
