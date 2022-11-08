package es.gobcan.istac.edatos.external.users.config.properties;

import org.springframework.context.annotation.Configuration;

import es.gobcan.istac.edatos.external.users.core.service.MetadataConfigurationService;

@Configuration
public class KafkaProperties {

    private static final String GROUP_ID = "edatos-external-users";

    private static final String CLIENT_ID = "edatos-external-users-client";

    private final MetadataConfigurationService metadataConfigurationService;

    public KafkaProperties(MetadataConfigurationService metadataConfigurationService) {
        this.metadataConfigurationService = metadataConfigurationService;
    }

    public String getOperationPublicationTopic() {
        return metadataConfigurationService.retrieveKafkaTopicOperationsPublication();
    }

    public String getDatasetPublicationTopic() {
        return metadataConfigurationService.retrieveKafkaTopicDatasetsPublication();
    }

    public String getDatasetExternalPublicationTopic() {
        return metadataConfigurationService.retrieveKafkaTopicExternalDatasetPublication();
    }
    
    public String getBootStrapServers() {
        return metadataConfigurationService.retrieveKafkaBootStrapServers();
    }

    public String getSchemaRegistryUrl() {
        return metadataConfigurationService.retrieveKafkaSchemaRegistryUrl();
    }

    public String getGroupId() {
        return KafkaProperties.GROUP_ID;
    }

    public String getClientId() {
        return KafkaProperties.CLIENT_ID;
    }
}
