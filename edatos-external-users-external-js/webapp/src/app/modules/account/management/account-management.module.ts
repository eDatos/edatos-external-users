import { CommonModule } from '@angular/common';
import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { SharedModule } from '@app/shared';
import { NbDialogModule } from '@nebular/theme';
import { ArteAuditInfoModule, ArteAutocompleteModule, ArteCalendarModule, ArteEntityListEmptyModule, ArteSpinnerModule, ArteTriInputSwitchModule } from 'arte-ng';
import { ArteDirectivesModule } from 'arte-ng/directives';
import { AccountManagementRoutingModule } from './account-management-routing.module';
import { AccountManagementComponent } from './account-management.component';

@NgModule({
    imports: [
        SharedModule,
        AccountManagementRoutingModule,
        NbDialogModule.forChild({ autoFocus: false }),
        ArteDirectivesModule,
        ArteAutocompleteModule,
        ArteTriInputSwitchModule,
        ArteAuditInfoModule,
        ArteSpinnerModule,
        ArteEntityListEmptyModule,
        CommonModule,
        ArteCalendarModule,
    ],
    declarations: [AccountManagementComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class AccountManagementModule {}
