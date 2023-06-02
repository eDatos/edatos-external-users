import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { SharedModule } from '@app/shared';
import { SignupRoutingModule, SignupFormComponent } from '.';
import { ArteCalendarModule } from 'arte-ng';
import { DataProtectionPolicyService } from '@app/modules/account/data-protection-policy/data-protection-policy.service';

@NgModule({
    imports: [SharedModule, SignupRoutingModule, ArteCalendarModule],
    declarations: [SignupFormComponent],
    providers: [DataProtectionPolicyService],
    entryComponents: [SignupFormComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class SignupModule {}
