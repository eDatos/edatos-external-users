package es.gobcan.istac.edatos.external.users.core.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import es.gobcan.istac.edatos.external.users.core.config.Constants;
import es.gobcan.istac.edatos.external.users.core.config.DocumentConstants;
import es.gobcan.istac.edatos.external.users.core.config.MetadataProperties;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.AuditPrincipalType;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FavoriteEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FilterEntity;
import es.gobcan.istac.edatos.external.users.core.domain.vo.AuditEventVO;
import es.gobcan.istac.edatos.external.users.core.domain.vo.ExternalUserVO;
import es.gobcan.istac.edatos.external.users.core.errors.CustomParameterizedExceptionBuilder;
import es.gobcan.istac.edatos.external.users.core.errors.ErrorConstants;
import es.gobcan.istac.edatos.external.users.core.i18n.LocaleContext;
import es.gobcan.istac.edatos.external.users.core.mapper.AuditEventMapper;
import es.gobcan.istac.edatos.external.users.core.mapper.ExternalUserVOMapper;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import es.gobcan.istac.edatos.external.users.core.service.AuditEventService;
import es.gobcan.istac.edatos.external.users.core.service.DocumentService;
import es.gobcan.istac.edatos.external.users.core.service.FilterService;
import es.gobcan.istac.edatos.external.users.core.service.adapter.JasperReportsAdapter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class DocumentServiceImpl implements DocumentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentServiceImpl.class);

    private final ExternalUserRepository externalUserRepository;
    private final FilterService filterService;
    private final JasperReportsAdapter jasperReportsService;
    private final MessageSource messageSource;
    private final MetadataProperties metadataProperties;
    private final AuditEventService auditEventService;
    private final AuditEventMapper auditEventMapper;
    private final ExternalUserVOMapper externalUserVOMapper;

    public DocumentServiceImpl(ExternalUserRepository externalUserRepository, JasperReportsAdapter jasperReportsService, MessageSource messageSource, FilterService filterService,
            MetadataProperties metadataProperties, AuditEventService auditEventService, AuditEventMapper auditEventMapper, ExternalUserVOMapper externalUserVOMapper) {
        this.externalUserRepository = externalUserRepository;
        this.jasperReportsService = jasperReportsService;
        this.messageSource = messageSource;
        this.filterService = filterService;
        this.metadataProperties = metadataProperties;
        this.auditEventService = auditEventService;
        this.auditEventMapper = auditEventMapper;
        this.externalUserVOMapper = externalUserVOMapper;
    }

    @Override
    public byte[] generatePdfExternalUserInformation(Long id) {
        try {
            ExternalUserEntity externalUser = externalUserRepository.findOne(id);
            ExternalUserVO externalUserVO = externalUserVOMapper.toDto(externalUser);

            List<FilterEntity> filters = filterService.findAllByUser(externalUser);
            Set<FavoriteEntity> fav = externalUser.getFavorites();
            List<AuditEvent> auditEvents = auditEventService.findByPrincipalAndPrincipalType(id.toString(), AuditPrincipalType.EXTERNAL_USER);
            List<AuditEventVO> auditEventsDtos = auditEvents.stream().map(auditEventMapper::toDto).collect(Collectors.toList());

            LOGGER.info("Request to print all information about External User");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("PERSONAL_INFORMATION", externalUserVO);
            parameters.put("FAVORITES", new JRBeanCollectionDataSource(fav));
            parameters.put("PERMALINK_PATH", metadataProperties.getVisualizerPath() + Constants.PORTAL_PATH_PERMALINK);
            parameters.put("FILTERS", new JRBeanCollectionDataSource(filters));
            parameters.put("AUDIT", new JRBeanCollectionDataSource(auditEventsDtos));
            parameters.put("PATH_LOGO", new URI(getClass().getResource(DocumentConstants.FOLDER_JASPER_REPORT + DocumentConstants.LOGO_CABECERA).toString()).getPath());

            // Headers
            Locale locale = LocaleContext.getLocale();
            String language = locale.toString().split("_")[0];
            parameters.put("LOCALE", language);
            parameters.put("DEFAULT_LOCALE", LocaleContext.getDefaultLocale().toString());

            parameters.put("HEADER_TITLE", messageSource.getMessage("report.header.title", null, locale));

            parameters.put("HEADER_PERSONAL_INFORMATION", messageSource.getMessage("report.header.personal_information", null, locale));
            parameters.put("HEADER_NAME_AND_SURNAMES", messageSource.getMessage("report.header.name_and_surnames", null, locale));
            parameters.put("HEADER_BIRTHDAY", messageSource.getMessage("report.header.birthday", null, locale));
            parameters.put("HEADER_EMAIL", messageSource.getMessage("report.header.email", null, locale));
            parameters.put("HEADER_ORGANISM", messageSource.getMessage("report.header.organism", null, locale));
            parameters.put("HEADER_GENDER", messageSource.getMessage("report.header.gender", null, locale));
            parameters.put("HEADER_NATIONALITY", messageSource.getMessage("report.header.nationality", null, locale));
            parameters.put("HEADER_LANGUAGE", messageSource.getMessage("report.header.language", null, locale));
            parameters.put("HEADER_TELEPHONE", messageSource.getMessage("report.header.telephone", null, locale));

            parameters.put("HEADER_CREATED_DATE", messageSource.getMessage("report.header.create_date", null, locale));
            parameters.put("HEADER_NAME", messageSource.getMessage("report.header.name", null, locale));

            parameters.put("HEADER_FILTERS", messageSource.getMessage("report.header.filters", null, locale));
            parameters.put("HEADER_FILTERS_LAST_ACCESS_DATE", messageSource.getMessage("report.header.filters.last_access_date", null, locale));
            parameters.put("HEADER_FILTERS_PERMALINK", messageSource.getMessage("report.header.filters.permalink", null, locale));
            parameters.put("HEADER_FILTERS_NOTES", messageSource.getMessage("report.header.filters.notes", null, locale));

            parameters.put("HEADER_FAVORITES", messageSource.getMessage("report.header.favorites", null, locale));
            parameters.put("HEADER_FAVORITES_URN_EXTERNAL_CATEGORY", messageSource.getMessage("report.header.favorites.urn_external_category", null, locale));
            parameters.put("HEADER_FAVORITES_PARENT", messageSource.getMessage("report.header.favorites.parent", null, locale));

            parameters.put("HEADER_AUDIT", messageSource.getMessage("report.header.audit", null, locale));
            parameters.put("HEADER_AUDIT_TYPE", messageSource.getMessage("report.header.audit.type", null, locale));
            parameters.put("HEADER_AUDIT_EXTRA_DATA", messageSource.getMessage("report.header.audit.extra_data", null, locale));

            return jasperReportsService.generateFromTemplate(DocumentConstants.EXTERNAL_USER_INFORMATION, parameters);
        } catch (URISyntaxException e) {
            throw new CustomParameterizedExceptionBuilder().message(DocumentConstants.EXCEPTION_PATH_LOGO_HEADER_EDATOS).code(ErrorConstants.ERR_GENERATED_PDF).build();

        }
    }
}
