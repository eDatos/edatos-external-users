import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ResetPasswordFinishComponent } from './finish/reset-password-finish.component';
import { ResetPasswordInitComponent } from './init/reset-password-init.component';

export const RESET_PASSWORD_ROUTES: Routes = [
    {
        path: '',
        component: ResetPasswordInitComponent,
        data: {
            authorities: [],
            pageTitle: 'passwordReset.init.title',
        },
    },
    {
        path: 'change-password',
        component: ResetPasswordFinishComponent,
        data: {
            authorities: [],
            pageTitle: 'passwordReset.finish.home.title',
        },
    },
];

@NgModule({
    imports: [RouterModule.forChild(RESET_PASSWORD_ROUTES)],
    exports: [RouterModule],
})
export class PasswordResetInitRoutingModule {}
