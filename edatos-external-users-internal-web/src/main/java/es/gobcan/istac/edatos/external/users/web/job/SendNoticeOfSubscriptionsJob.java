package es.gobcan.istac.edatos.external.users.web.job;

import es.gobcan.istac.edatos.external.users.core.domain.CategoryEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalDatasetEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalOperationEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FavoriteEntity;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.*;
import org.siemac.edatos.core.common.exception.EDatosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SendNoticeOfSubscriptionsJob extends AbstractConsumerQuartzJob {

    private final Logger log = LoggerFactory.getLogger(SendNoticeOfSubscriptionsJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) {
        log.info("--- INIT --- Job SendNoticeOfSubscriptionsJob running");
        createNotice(context);
    }

    private void createNotice(JobExecutionContext context) {
        log.info("Job SendNoticeOfSubscriptionsJob running into transaction");
        try {
            List<ExternalDatasetEntity> updatedDatasets = getExternalDatasetService(context).getLastDayDatasets();
            Map<String, List<ExternalDatasetEntity>> updatedDatasetsGroupedByOperation = updatedDatasets.stream()
                .collect(Collectors.groupingBy(ExternalDatasetEntity::getExternalOperationUrn));

            if (CollectionUtils.isNotEmpty(updatedDatasets)) {
                List<ExternalOperationEntity> operationsOfUpdatedDatasets = getExternalOperationService(context).findByExternalOperationDatasetUrnIn(updatedDatasets);
                for (ExternalOperationEntity operation : operationsOfUpdatedDatasets) {
                    List<ExternalDatasetEntity> operationDatasets = updatedDatasetsGroupedByOperation.get(operation.getUrn());
                    List<CategoryEntity> operationAncestors = getOperationAncestors(operation, context);
                    boolean operationIsInTheCategoryTree = !operationAncestors.isEmpty();
                    if (operation.isEnabled() && operation.isNotificationsEnabled() && operationIsInTheCategoryTree) {
                        List<FavoriteEntity> favoritesOfOperationOrAncestors = getFavoriteService(context).findByExternalOperationOrCategory(Collections.singletonList(operation), operationAncestors);

                        if (CollectionUtils.isNotEmpty(favoritesOfOperationOrAncestors)) {
                            Set<ExternalUserEntity> usersSubscribedToOperation = favoritesOfOperationOrAncestors.stream().map(FavoriteEntity::getExternalUser).collect(Collectors.toSet());
                            getNotificationService(context).createNoticeOfSubscriptionsJob(usersSubscribedToOperation, operation, operationDatasets);
                        }
                    }
                    deleteDatasets(operationDatasets, context);
                }
            }
        } catch (Exception e) {
            log.error("SendNoticeOfSubscriptionsJob error: ", e);
            throw new EDatosException();
        }

        log.info("--- FINISHED --- Job SendNoticeOfSubscriptionsJob finished.");
    }

    private List<CategoryEntity> getOperationAncestors(ExternalOperationEntity operation, JobExecutionContext context) {
        List<CategoryEntity> operationAncestors = new ArrayList<>();
        CategoryEntity operationAncestorCategory = getCategoryService(context).findCategoryByExternalOperation(operation);
        if (operationAncestorCategory != null) {
            operationAncestors.add(operationAncestorCategory);
            while (operationAncestorCategory.getParent() != null) {
                operationAncestors.add(operationAncestorCategory.getParent());
                operationAncestorCategory = operationAncestorCategory.getParent();
            }
        }
        return operationAncestors;
    }

    private void deleteDatasets(List<ExternalDatasetEntity> listDataset, JobExecutionContext context) {
        PlatformTransactionManager platformTransactionManager = getPlatformTransactionManager(context);
        TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);

        transactionTemplate.execute(status -> {
            listDataset.forEach(dataset -> getExternalDatasetService(context).delete(dataset));
            return true;
        });
    }
}
