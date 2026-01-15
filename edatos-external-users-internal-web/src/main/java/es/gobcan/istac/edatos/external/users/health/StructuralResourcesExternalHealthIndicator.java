package es.gobcan.istac.edatos.external.users.health;

import static es.gobcan.istac.edatos.external.users.core.config.Constants.HEALTH_INDICATOR_REQUEST_TIMEOUT_MS;

import org.apache.commons.lang.StringUtils;
import org.siemac.edatos.core.common.constants.CoreCommonConstants;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import es.gobcan.istac.edatos.external.users.core.service.MetadataConfigurationService;

@Component
public class StructuralResourcesExternalHealthIndicator extends AbstractHealthIndicator {
    private final MetadataConfigurationService metadataConfigurationService;

    public StructuralResourcesExternalHealthIndicator(MetadataConfigurationService metadataConfigurationService) {
        this.metadataConfigurationService = metadataConfigurationService;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        String endpoint = metadataConfigurationService.retrieveSrmExternalApiUrlBase() + "?_wadl";
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(HEALTH_INDICATOR_REQUEST_TIMEOUT_MS);
        factory.setReadTimeout(HEALTH_INDICATOR_REQUEST_TIMEOUT_MS);
        RestTemplate restTemplate = new RestTemplate(factory);
        HttpHeaders headers = new HttpHeaders();
        String apiKey = metadataConfigurationService.retrieveApiKey();
        if (StringUtils.isNotBlank(apiKey)) {
            headers.set(CoreCommonConstants.API_KEY_PARAMETER, apiKey);
        }
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        try {
            restTemplate.exchange(endpoint, HttpMethod.GET, requestEntity, String.class);
            builder.withDetail("endpoint", endpoint).up();
        } catch (Exception e) {
            builder.withException(e).down();
        }
    }
}
