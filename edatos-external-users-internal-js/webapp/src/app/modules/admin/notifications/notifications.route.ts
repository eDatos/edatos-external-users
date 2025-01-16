import { RouterModule, Routes } from '@angular/router';
import { NotificationsComponent } from './notifications.component';
import { NotificationsDetailComponent } from "./notifications-detail/notifications-detail.component";
import { NgModule } from "@angular/core";
import { NotificationsTableComponent } from "@app/modules/admin/notifications/notifications-table/notifications-table.component";

export const notificationsRoutes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        component: NotificationsTableComponent,
        data: {
            pageTitle: 'notifications.home.title',
        },
    },
    {
        path: 'new',
        component: NotificationsComponent,
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
