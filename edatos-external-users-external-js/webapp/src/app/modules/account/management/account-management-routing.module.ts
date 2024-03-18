import { RouterModule, Routes } from '@angular/router';

import { AccountManagementComponent } from './account-management.component';
import { UserRouteAccessGuard } from '@app/core/guard/user-route-access.guard';
import { NgModule } from '@angular/core';
import { Role } from '@app/core/model';
import { UserResolver } from '@app/core/resolver';
import {
    AccountManagementEditComponent
} from '@app/modules/account/management/account-management-edit/account-management-edit.component';
import { CompleteEmailModificationComponent } from "@app/modules/account/management/complete-email-modification/complete-email-modification.component";

export const accountManagementRoutes: Routes = [
    {
        path: '',
        component: AccountManagementComponent,
        data: {
            pageTitle: 'account.title',
            roles: [Role.USER]
        },
        resolve: {
            user: UserResolver
        },
        canActivate: [UserRouteAccessGuard]
    },
    {
        path: 'edit',
        component: AccountManagementEditComponent,
        data: {
            pageTitle: 'account.title',
            roles: [Role.USER]
        },
        resolve: {
            user: UserResolver
        },
        canActivate: [UserRouteAccessGuard]
    },
    {
        path: 'complete-email-modification',
        component: CompleteEmailModificationComponent
    }
];

@NgModule({
    imports: [RouterModule.forChild(accountManagementRoutes)],
    exports: [RouterModule]
})
export class AccountManagementRoutingModule {
}
