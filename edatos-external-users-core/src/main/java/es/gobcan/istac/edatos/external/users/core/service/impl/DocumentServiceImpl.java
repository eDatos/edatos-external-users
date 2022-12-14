package es.gobcan.istac.edatos.external.users.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.gobcan.istac.edatos.external.users.core.config.DocumentConstants;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FilterEntity;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import es.gobcan.istac.edatos.external.users.core.repository.FilterRepository;
import es.gobcan.istac.edatos.external.users.core.service.DocumentService;
import es.gobcan.istac.edatos.external.users.core.service.adapter.JasperReportsAdapter;
import net.sf.jasperreports.engine.JRParameter;

@Service
public class DocumentServiceImpl implements DocumentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentServiceImpl.class);

    private final ExternalUserRepository externalUserRepository;

    private final FilterRepository filterRepository;

    private final JasperReportsAdapter jasperReportsService;



    public DocumentServiceImpl(ExternalUserRepository externalUserRepository, FilterRepository filterRepository, JasperReportsAdapter jasperReportsService) {
        this.externalUserRepository = externalUserRepository;
        this.filterRepository = filterRepository;
        this.jasperReportsService = jasperReportsService;
    }

    @Override
    public byte[] generatePdfExternalUserInformation(Long id) {

            ExternalUserEntity externalUser = externalUserRepository.findOne(id);
            List<FilterEntity> filters = filterRepository.findAllByExternalUserOrderByCreatedDate(externalUser);

            LOGGER.debug("Request to print all information about External User");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("PERSONAL_INFORMATION", externalUser);
            parameters.put("FAVORITES", externalUser.getFavorites());
            parameters.put("FILTERS", filters);
            parameters.put(JRParameter.REPORT_LOCALE, DocumentConstants.PARAM_LOCALE_ES);

            return jasperReportsService.generateFromTemplate(DocumentConstants.EXTERNAL_USER_INFORMATION, parameters);
    }
}
