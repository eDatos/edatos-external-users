import { RouterModule, Routes } from '@angular/router';
import { NotificationsFormComponent } from './notifications-form/notifications-form.component';
import { NotificationsDetailComponent } from "./notifications-detail/notifications-detail.component";
import { NgModule } from "@angular/core";
import { NotificationsRecordComponent } from "@app/modules/admin/notifications/notifications-record/notifications-record.component";

export const notificationsRoutes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        component: NotificationsRecordComponent,
        data: {
            pageTitle: 'notifications.home.title',
        },
    },
    {
        path: 'new',
        component: NotificationsFormComponent,
        data: {
            pageTitle: 'notifications.home.title',
        }
    },
    {
        path: ':id',
        component: NotificationsDetailComponent,
        data: {
            pageTitle: 'notifications.home.title',
        }
    }
];

@NgModule({
    imports: [RouterModule.forChild(notificationsRoutes)],
    exports: [RouterModule],
})
export class NotificationsRoutingModule {}
