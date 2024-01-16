import { CommonModule } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { SharedModule } from '@app/shared';
import { NbDialogModule } from '@nebular/theme';
import {
    ArteAuditInfoModule,
    ArteAutocompleteModule,
    ArteCalendarModule,
    ArteEntityListEmptyModule,
    ArteSpinnerModule,
    ArteTriInputSwitchModule
} from 'arte-ng';
import { ArteDirectivesModule } from 'arte-ng/directives';
import { AccountManagementRoutingModule } from './account-management-routing.module';
import { AccountManagementComponent } from './account-management.component';
import {
    AccountManagementEditComponent
} from '@app/modules/account/management/account-management-edit/account-management-edit.component';

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
        ArteCalendarModule
    ],
    declarations: [AccountManagementComponent, AccountManagementEditComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AccountManagementModule {
}
