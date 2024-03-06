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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SendNoticeOfSubscriptionsJob extends AbstractConsumerQuartzJob {

    private final Logger log = LoggerFactory.getLogger(SendNoticeOfSubscriptionsJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("--- INIT --- Job SendNoticeOfSubscriptionsJob running");
        createNotice(context);
    }

    private void createNotice(JobExecutionContext context) {

        PlatformTransactionManager platformTransactionManager = getPlatformTransactionManager(context);
        TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);

        transactionTemplate.execute(new TransactionCallback<Boolean>() {

            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                log.info("Job SendNoticeOfSubscriptionsJob running into transaction");
                try {
                    List<ExternalDatasetEntity> updatedDatasets = getExternalDatasetService(context).getLastDayDatasets();

                    if (CollectionUtils.isNotEmpty(updatedDatasets)) {
                        List<ExternalOperationEntity> operationsOfUpdatedDatasets = getExternalOperationService(context).findByExternalOperationDatasetUrnIn(updatedDatasets);
                        for (ExternalOperationEntity operation : operationsOfUpdatedDatasets) {
                            if (operation.isEnabled() && operation.isNotificationsEnabled()) {
                                List<CategoryEntity> operationAncestors = getOperationAncestors(operation, context);
                                List<FavoriteEntity> favoritesOfOperationOrAncestors = getFavoriteService(context)
                                    .findByExternalOperationOrCategory(Collections.singletonList(operation), operationAncestors);

                                if (CollectionUtils.isNotEmpty(favoritesOfOperationOrAncestors)) {
                                    Set<ExternalUserEntity> usersSubscribedToOperation = favoritesOfOperationOrAncestors
                                        .stream().map(FavoriteEntity::getExternalUser).collect(Collectors.toSet());
                                    getNotificationService(context).createNoticeOfSubscriptionsJob(usersSubscribedToOperation, operation, updatedDatasets);
                                    deleteDatasetsByOperation(updatedDatasets, operation, context);
                                }
                            }
                        }
                    }

                    return true;
                } catch (Exception e) {
                    log.error("SendNoticeOfSubscriptionsJob error: ", e);
                    throw new EDatosException();
                }
            }

        });
        log.info("--- FINISHED --- Job SendNoticeOfSubscriptionsJob finished.");
    }

    private List<CategoryEntity> getOperationAncestors(ExternalOperationEntity operation, JobExecutionContext context) {
        List<CategoryEntity> operationAncestors = new ArrayList<>();
        CategoryEntity operationAncestorCategory = getCategoryService(context).findCategoryByExternalOperation(operation);
        operationAncestors.add(operationAncestorCategory);
        while (operationAncestorCategory.getParent() != null) {
            operationAncestors.add(operationAncestorCategory.getParent());
            operationAncestorCategory = operationAncestorCategory.getParent();
        }
        return operationAncestors;
    }

    private void deleteDatasetsByOperation(List<ExternalDatasetEntity> listDataset, ExternalOperationEntity operation, JobExecutionContext context) {
        for (ExternalDatasetEntity dataset : listDataset) {
            if (dataset.getExternalOperationUrn().equals(operation.getUrn())) {
                getExternalDatasetService(context).delete(dataset);
            }
        }
    }
}
