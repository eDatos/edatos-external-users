package es.gobcan.istac.edatos.external.users.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import es.gobcan.istac.edatos.external.users.core.config.Constants;
import es.gobcan.istac.edatos.external.users.core.config.DocumentConstants;
import es.gobcan.istac.edatos.external.users.core.config.MetadataProperties;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FavoriteEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FilterEntity;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.PermalinkType;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
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

    public DocumentServiceImpl(ExternalUserRepository externalUserRepository, JasperReportsAdapter jasperReportsService, MessageSource messageSource, FilterService filterService
    , MetadataProperties metadataProperties) {
        this.externalUserRepository = externalUserRepository;
        this.jasperReportsService = jasperReportsService;
        this.messageSource = messageSource;
        this.filterService = filterService;
        this.metadataProperties = metadataProperties;
    }

    @Override
    public byte[] generatePdfExternalUserInformation(Long id) {

        ExternalUserEntity externalUser = externalUserRepository.findOne(id);

        List<FilterEntity> filters = filterService.findAllByUser(externalUser);
        Set<FavoriteEntity> fav = externalUser.getFavorites();

        LOGGER.info("Request to print all information about External User");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("PERSONAL_INFORMATION", externalUser);
        parameters.put("FAVORITES", new JRBeanCollectionDataSource(fav));
        parameters.put("PERMALINK_PATH", metadataProperties.getVisualizerPath() + Constants.PATH_PERMALINK );
        parameters.put("FILTERS", new JRBeanCollectionDataSource(filters));

        // Headers
        Locale locale = LocaleContextHolder.getLocale();
        String language = locale.toString().split("_")[0];
        parameters.put("LOCALE", language);

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

        parameters.put("HEADER_FILTERS", messageSource.getMessage("report.header.filters", null, locale));
        parameters.put("HEADER_FILTERS_NAME", messageSource.getMessage("report.header.filters.name", null, locale));
        parameters.put("HEADER_FILTERS_CREATED_DATE", messageSource.getMessage("report.header.filters.create_date", null, locale));
        parameters.put("HEADER_FILTERS_LAST_ACCESS_DATE", messageSource.getMessage("report.header.filters.last_access_date", null, locale));
        parameters.put("HEADER_FILTERS_PERMALINK", messageSource.getMessage("report.header.filters.permalink", null, locale));
        parameters.put("HEADER_FILTERS_NOTES", messageSource.getMessage("report.header.filters.notes", null, locale));

        parameters.put("HEADER_FAVORITES", messageSource.getMessage("report.header.favorites", null, locale));
        parameters.put("HEADER_FAVORITES_NAME", messageSource.getMessage("report.header.favorites.name", null, locale));
        parameters.put("HEADER_FAVORITES_URN_EXTERNAL_CATEGORY", messageSource.getMessage("report.header.favorites.urn_external_category", null, locale));
        parameters.put("HEADER_FAVORITES_PARENT", messageSource.getMessage("report.header.favorites.parent", null, locale));

        return jasperReportsService.generateFromTemplate(DocumentConstants.EXTERNAL_USER_INFORMATION, parameters);
    }
}
