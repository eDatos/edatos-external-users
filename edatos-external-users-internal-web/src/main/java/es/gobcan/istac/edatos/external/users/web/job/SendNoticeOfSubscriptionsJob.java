package es.gobcan.istac.edatos.external.users.web.job;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalDatasetEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalOperationEntity;
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

import java.util.List;

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
                    List<ExternalDatasetEntity> listDataset = getExternalDatasetService(context).getLastDayDatasets();

                    if (CollectionUtils.isNotEmpty(listDataset)) {
                        List<ExternalOperationEntity> listOperations = getExternalOperationService(context).findByExternalOperationDatasetUrnIn(listDataset);

                        List<FavoriteEntity> listFavorites = getFavoriteService(context).findByExternalOperation(listOperations);

                        if (CollectionUtils.isNotEmpty(listFavorites)) {
                            getNotificationService(context).createNoticeOfSusbcritionsJob(listFavorites, listDataset);
                            deleteDatasets(listDataset, context);
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

    private void deleteDatasets(List<ExternalDatasetEntity> listDataset, JobExecutionContext context) {
        listDataset.stream().forEach(dataset -> getExternalDatasetService(context).delete(dataset));
    }
}
