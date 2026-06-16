package es.gobcan.istac.edatos.external.users.service.impl;

import org.apache.avro.specific.SpecificData;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.siemac.metamac.statistical.operations.core.stream.messages.OperationAvro;
import org.siemac.metamac.statistical.resources.core.stream.messages.DatasetVersionAvro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalDatasetEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalOperationEntity;
import es.gobcan.istac.edatos.external.users.core.service.ExternalDatasetService;
import es.gobcan.istac.edatos.external.users.core.service.ExternalOperationService;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.ExternalDatasetMapper;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.ExternalOperationMapper;
import es.gobcan.istac.edatos.external.users.service.KafkaConsumerService;
import es.ibestat.jaxi.stream.messages.DatasetAvro;
import es.ibestat.jaxi.stream.messages.ProcStatusEnumAvro;
import java.util.Optional;

import static org.siemac.metamac.statistical.operations.core.stream.messages.ProcStatusEnumAvro.PUBLISH_EXTERNALLY;

@Service
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerServiceImpl.class);

    private final ExternalOperationMapper externalOperationMapper;
    private final ExternalOperationService externalOperationService;
    private final ExternalDatasetMapper externalDatasetMapper;
    private final ExternalDatasetService externalDatasetService;

    public KafkaConsumerServiceImpl(ExternalOperationMapper externalOperationMapper, ExternalOperationService externalOperationService, ExternalDatasetMapper externalDatasetMapper,
        ExternalDatasetService externalDatasetService) {
        this.externalOperationMapper = externalOperationMapper;
        this.externalOperationService = externalOperationService;
        this.externalDatasetMapper = externalDatasetMapper;
        this.externalDatasetService = externalDatasetService;
    }

    /**
     * Handles datasets coming from eDatos.
     */
    @KafkaListener(id = "datasetPublicationTopicId", topics = "#{kafkaProperties.getDatasetPublicationTopic()}")
    public void processDatasetPublicationEvent(ConsumerRecord<String, DatasetVersionAvro> consumerRecord) {
        String urn = consumerRecord.value()
                                   .getSiemacMetadataStatisticalResource()
                                   .getLifecycleStatisticalResource()
                                   .getVersionableStatisticalResource()
                                   .getNameableStatisticalResource()
                                   .getIdentifiableStatisticalResource()
                                   .getUrn();
        LOGGER.info("Kafka dataset message received. Code: '{}'", urn);

        // Datasets with patch > 0 are ignored because minor modifications to datasets
        // do not get notified to users.
        if (consumerRecord.value()
                          .getSiemacMetadataStatisticalResource()
                          .getLifecycleStatisticalResource()
                          .getVersionableStatisticalResource()
                          .getPatch() != 0) {
            LOGGER.info("Ignoring dataset {} because is a patch version. No notification will be sent", urn);
            return;
        }

        ExternalDatasetEntity dataset = externalDatasetMapper.toEntity(consumerRecord.value());
        Optional<ExternalDatasetEntity> inDbDataset = externalDatasetService.findByUrn(urn);
        if (inDbDataset.isPresent()) {
            dataset.setId(inDbDataset.get().getId());
            externalDatasetService.update(dataset);
        } else {
            externalDatasetService.create(dataset);
        }
    }

    /**
     * Handles datasets coming from GPE/Jaxi.
     */
    @KafkaListener(id = "externalPublicationTopicId", topics = "#{kafkaProperties.getDatasetExternalPublicationTopic()}")
    public void processDatasetExternalPublicationEvent(ConsumerRecord<String, DatasetAvro> consumerRecord) {
        String urn = consumerRecord.value().getUrn();
        LOGGER.info("Kafka external publication dataset message received. Code: '{}'", urn);
        DatasetAvro datasetRecord = SpecificData.get().deepCopy(DatasetAvro.SCHEMA$, consumerRecord.value());
        if (!hasValidStatisticalOperation(datasetRecord)) {
            return;
        }
        ExternalDatasetEntity dataset = externalDatasetMapper.toEntity(datasetRecord);

        Optional<ExternalDatasetEntity> inDbDataset = externalDatasetService.findByUrn(urn);
        if (inDbDataset.isPresent()) {
            dataset.setId(inDbDataset.get().getId());

            if (datasetRecord.getProcStatus() == ProcStatusEnumAvro.ARCHIVED) {
                externalDatasetService.delete(dataset);
            } else {
                externalDatasetService.update(dataset);
            }
        } else {
             if (datasetRecord.getProcStatus() != ProcStatusEnumAvro.ARCHIVED) {
                externalDatasetService.create(dataset);
             }
        }
    }

    private boolean hasValidStatisticalOperation(DatasetAvro datasetRecord) {
        if (datasetRecord.getStatisticalOperation() == null || datasetRecord.getStatisticalOperation().getCode() == null) {
            LOGGER.error("[external-users] Jaxi dataset with null statistical operation received from Kafka. Dataset URN: {}", datasetRecord.getUrn());
            return false;
        }
        return true;
    }

    @KafkaListener(id = "operationPublicationTopicId", topics = "#{kafkaProperties.getOperationPublicationTopic()}")
    public void processOperationPublicationEvent(ConsumerRecord<String, OperationAvro> consumerRecord) {
        LOGGER.info("Kafka operation message received. Code: '{}'", consumerRecord.value().getUrn());
        if (consumerRecord.value().getProcStatus() != PUBLISH_EXTERNALLY) {
            LOGGER.info("Operation {} is not published externally so it will be ignored", consumerRecord.value().getUrn());
            return;
        }
        ExternalOperationEntity operation = externalOperationMapper.toEntity(consumerRecord.value());
        Optional<ExternalOperationEntity> inDbOperation = externalOperationService.findByUrn(operation.getUrn());
        if (inDbOperation.isPresent()) {
            operation.setId(inDbOperation.get().getId());
            externalOperationService.update(operation);
        } else {
            externalOperationService.create(operation);
        }
    }
}
