import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { SharedModule } from '@app/shared';
import { LoginRoutingModule, LoginComponent } from '.';
import { PasswordModule } from 'primeng/password';

@NgModule({
    imports: [SharedModule, LoginRoutingModule, PasswordModule],
    declarations: [LoginComponent],
    entryComponents: [LoginComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class LoginModule {}
