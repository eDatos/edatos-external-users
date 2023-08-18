import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { SharedModule } from '@app/shared';
import { SignupFormComponent, SignupRoutingModule } from '.';
import {
    DataProtectionPolicyService
} from '@app/modules/account/data-protection-policy/data-protection-policy.service';

@NgModule({
    imports: [SharedModule, SignupRoutingModule],
    declarations: [SignupFormComponent],
    providers: [DataProtectionPolicyService],
    entryComponents: [SignupFormComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SignupModule {
}
