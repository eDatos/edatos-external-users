import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { categoryMasterTreeRoutes } from '@app/modules/admin/category-master-tree/category-master-tree.route';
import { externalOperationsListRoutes } from '@app/modules/admin/external-operations-list/external-operations-list.route';
import { auditsRoute } from './audits/audits.route';
import { configurationRoute } from './configuration/configuration.route';
import { dataProtectionPolicyConfigurationRoute } from './data-protection-policy/data-protection-policy.route';
import { healthRoute } from './health/health.route';
import { logsRoute } from './logs/logs.route';
import { metricsRoute } from './metrics/metrics.route';

export const adminRoutes: Routes = [
    auditsRoute,
    configurationRoute,
    healthRoute,
    logsRoute,
    metricsRoute,
    dataProtectionPolicyConfigurationRoute,
    categoryMasterTreeRoutes,
    auditsRoute,
    externalOperationsListRoutes,
    {
        path: 'notifications',
        loadChildren: () => import('./notifications/notifications.module').then((m) => m.NotificationsModule)
    }
];

@NgModule({
    imports: [RouterModule.forChild(adminRoutes)],
    exports: [RouterModule],
})
export class AdminRoutingModule {}
