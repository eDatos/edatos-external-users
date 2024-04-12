import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SignupFormComponent } from './signup.component';
import { ALL_ALLOWED } from '@app/core/service/auth';
import { NotLoggedUserCanAccessGuard } from '@app/core/guard/not-logged-user-can-access.guard';

export const signupRoutes: Routes = [
    {
        path: '',
        component: SignupFormComponent,
        data: {
            roles: ALL_ALLOWED,
            pageTitle: 'signup.home.title'
        },
        canActivate: [NotLoggedUserCanAccessGuard]
    }
];

@NgModule({
    imports: [RouterModule.forChild(signupRoutes)],
    exports: [RouterModule]
})
export class SignupRoutingModule {
}
