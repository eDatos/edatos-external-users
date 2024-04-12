import { RouterModule, Routes } from '@angular/router';

import { NotificationsComponent } from './notifications.component';
import { UserRouteAccessGuard } from '@app/core/guard/user-route-access.guard';
import { NgModule } from '@angular/core';
import { Role } from '@app/core/model';
import { UserResolver } from '@app/core/resolver';
import { ITEMS_PER_PAGE } from '@app/app.constants';
import { PagingParamsResolver } from 'arte-ng/services';
import {
    NotificationsDetailComponent
} from '@app/modules/account/notifications/notifications-detail/notifications-detail.component';

export const accountManagementRoutes: Routes = [
    {
        path: '',
        component: NotificationsComponent,
        data: {
            pageTitle: 'account.title',
            roles: [Role.USER],
            defaultPagingParams: {
                page: '1',
                sort: 'desc',
                size: ITEMS_PER_PAGE
            }
        },
        resolve: {
            pagingParams: PagingParamsResolver,
            user: UserResolver
        },
        canActivate: [UserRouteAccessGuard]
    },
    {
        path: ':id',
        component: NotificationsDetailComponent,
        resolve: {
            user: UserResolver
        },
        canActivate: [UserRouteAccessGuard]
    }
];

@NgModule({
    imports: [RouterModule.forChild(accountManagementRoutes)],
    exports: [RouterModule]
})
export class NotificationsRoutingModule {
}
