package es.gobcan.istac.edatos.external.users.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.TopicExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import es.gobcan.istac.edatos.external.users.config.properties.KafkaProperties;
import es.gobcan.istac.edatos.external.users.core.service.MetadataConfigurationService;

@Component
public class KafkaInitializeTopics {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaInitializeTopics.class);    
    private static final int NUM_OF_PARTITIONS = 1;
    private static final short NUM_OF_REPLICATION = (short) 1;
    private static final int TIMEOUT = 1000;
    private static final String RETENTION_MS = "retention.ms";
    private static final Map<String, String> TOPIC_DEFAULT_SETTINGS;

    static {
        TOPIC_DEFAULT_SETTINGS = new HashMap<>();
        TOPIC_DEFAULT_SETTINGS.put(RETENTION_MS, "-1");
    }

    private final MetadataConfigurationService metadataConfigurationService;
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    public KafkaInitializeTopics(MetadataConfigurationService metadataConfigurationService, KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry) {
        this.metadataConfigurationService = metadataConfigurationService;
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        ApplicationContext ac = event.getApplicationContext();
        if (ac.getParent() == null) {
            try {
                LOGGER.info("Initializing Kafka topics...");    
                propagateCreationOfTopics();
                startAvailableConsumers();
            } catch (Exception e) {
                LOGGER.error("Could not initialize Kafka topics", e);
            }
        }
    }
    
    private void startAvailableConsumers() {
        runConsumer(KafkaProperties.GROUP_ID + "-datasetPublicationTopicId");
        runConsumer(KafkaProperties.GROUP_ID + "-operationPublicationTopicId");
        NewTopic externalPublicationTopic = getExternalDatasetsPublicationTopic();
        if (externalPublicationTopic != null) {
            runConsumer(KafkaProperties.GROUP_ID + "-externalPublicationTopicId");
        }

    }

    private void runConsumer(String consumerId) {
        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(consumerId);
        if (listenerContainer != null && !listenerContainer.isRunning()) {
            listenerContainer.start();
        }
    }
    
    public Properties getKafkaProperties() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, metadataConfigurationService.retrieveKafkaBootStrapServers());
        return properties;
    }

    private void propagateCreationOfTopics() {
        Properties kafkaProperties = getKafkaProperties();
        List<NewTopic> topics = getTopics();
        CreateTopicsOptions topicsOptions = getTopicsOptions();
        LOGGER.info("Kafka Properties: {}, Topics: {}", kafkaProperties, topics);
        createTopics(kafkaProperties, topics, topicsOptions);
    }

    private List<NewTopic> getTopics() {
        List<NewTopic> topics = new ArrayList<>();
        topics.add(createTopic(metadataConfigurationService.retrieveKafkaTopicOperationsPublication()));
        topics.add(createTopic(metadataConfigurationService.retrieveKafkaTopicDatasetsPublication()));

        NewTopic externalPublicationTopic = getExternalDatasetsPublicationTopic();
        if (externalPublicationTopic != null) {
            topics.add(externalPublicationTopic);
        }
        return topics;
    }
    
    private NewTopic getExternalDatasetsPublicationTopic() {
        try {
            String topicNameDatasetExternalPublication = metadataConfigurationService.retrieveKafkaTopicExternalDatasetPublication();
            return createTopic(topicNameDatasetExternalPublication);
        } catch (Exception e) {
            LOGGER.error("It is not possible create external publication topic");
        }
        return null;
    }

    private static List<NewTopic> getAvailableTopics(AdminClient adminClient, List<NewTopic> topics) {
        // excludes nonexistent topics
        List<NewTopic> availableTopics = new ArrayList<>();
        try {
            ListTopicsResult listTopics = adminClient.listTopics();
            Set<String> names = listTopics.names().get();
            for (NewTopic topic : topics) {
                if (names.contains(topic.name())) {
                    availableTopics.add(topic);
                }
            }
        } catch (Exception e) {
            LOGGER.info("error to get list available topics");
        }
        return availableTopics;
    }
    
    private NewTopic createTopic(String topic) {
        return new NewTopic(topic, NUM_OF_PARTITIONS, NUM_OF_REPLICATION).configs(TOPIC_DEFAULT_SETTINGS);
    }

    private CreateTopicsOptions getTopicsOptions() {
        return new CreateTopicsOptions().timeoutMs(TIMEOUT);
    }

    private void createTopics(Properties kafkaProperties, List<NewTopic> topics, CreateTopicsOptions topicsOptions) {
        List<NewTopic> availableTopics = new ArrayList<>();
        try (AdminClient adminClient = AdminClient.create(kafkaProperties)) {
            availableTopics = getAvailableTopics(adminClient, topics);
            adminClient.createTopics(availableTopics, topicsOptions).all().get();
        } catch (KafkaException | ExecutionException e) {
            if (e.getCause() instanceof TopicExistsException) {
                LOGGER.debug("{}", e.getCause().getMessage());
            } else {
                throw new RuntimeException("Imposible to create/check Topic in Kafka", e);
            }
        } catch (InterruptedException e) {
            LOGGER.error("Thread interrupted, imposible to create/check Topic in Kafka", e);
            Thread.currentThread().interrupt(); // See SonarLint java:S2142
        }
    }
}
