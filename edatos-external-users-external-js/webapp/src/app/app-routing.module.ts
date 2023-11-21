import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ALL_ALLOWED, USER } from './core/service';
import { UserRouteAccessGuard } from './core/guard';
import { DEFAULT_PATH } from './app.constants';
import { NotLoggedUserCanAccessGuard } from './core/guard/not-logged-user-can-access.guard';


const APP_ROUTES: Routes = [
    {
        path: '',
        pathMatch: 'full',
        redirectTo: DEFAULT_PATH
    },
    {
        path: 'signup',
        loadChildren: () => import('./modules/signup/signup.module').then((m) => m.SignupModule),
        data: {
            roles: ALL_ALLOWED
        }
    },
    {
        path: 'login',
        loadChildren: () => import('./modules/login/login.module').then((m) => m.LoginModule),
        canLoad: [NotLoggedUserCanAccessGuard],
        data: {
            roles: ALL_ALLOWED
        }
    },
    {
        path: 'account-management',
        loadChildren: () => import('./modules/account/management/account-management.module').then((m) => m.AccountManagementModule),
        canLoad: [UserRouteAccessGuard],
        data: {
            roles: USER
        }
    },
    {
        path: 'notifications',
        loadChildren: () => import('./modules/account/notifications/notifications.module').then((m) => m.NotificationsModule),
        canLoad: [UserRouteAccessGuard],
        data: {
            roles: USER
        }
    },
    {
        path: 'change-password',
        loadChildren: () => import('./modules/account/password/change-password.module').then((m) => m.ChangePasswordModule),
        canLoad: [UserRouteAccessGuard],
        data: {
            roles: USER
        }
    },
    {
        path: 'filter',
        loadChildren: () => import('./modules/filter/filter.module').then((m) => m.FilterModule),
        canLoad: [UserRouteAccessGuard],
        data: {
            roles: USER
        }
    },
    {
        path: 'data-protection-policy',
        loadChildren: () => import('./modules/account/data-protection-policy/data-protection-policy.module').then((m) => m.DataProtectionPolicyModule),
        data: {
            roles: ALL_ALLOWED
        }
    },
    {
        path: 'favorite',
        loadChildren: () => import('./modules/favorite/favorite.module').then((m) => m.FavoriteModule),
        canLoad: [UserRouteAccessGuard],
        data: {
            roles: USER
        }
    },
    {
        path: 'reset-password',
        loadChildren: () => import('./modules/reset-password/reset-password.module').then((m) => m.ResetPasswordModule),
        data: {
            roles: ALL_ALLOWED
        }
    },
    {
        path: '**',
        redirectTo: 'notfound'
    }
];

@NgModule({
    imports: [RouterModule.forRoot(APP_ROUTES, { useHash: true })],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
