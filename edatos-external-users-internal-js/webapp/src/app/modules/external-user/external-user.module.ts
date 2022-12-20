import { CommonModule } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { ExternalUserService } from '@app/core/service';
import { ExternalUserDeleteDialogComponent } from '@app/modules/external-user/external-user-delete-dialog.component';
import { ExternalUserFormComponent } from '@app/modules/external-user/external-user-form/external-user-form.component';
import { ExternalUserListComponent } from '@app/modules/external-user/external-user-list/external-user-list.component';
import { ExternalUserRoutingModule } from '@app/modules/external-user/external-user-routing.module';
import { ExternalUserSearchComponent } from '@app/modules/external-user/external-user-search/external-user-search.component';
import { FilterModule } from '@app/modules/filter';
import { SharedModule } from '@app/shared';
import { ArteAuditInfoModule, ArteAutocompleteModule, ArteCalendarModule, ArteEntityListEmptyModule, ArteSpinnerModule, ArteTableModule, ArteTriInputSwitchModule } from 'arte-ng';
import { ArteDirectivesModule } from 'arte-ng/directives';
import { DialogModule } from 'primeng/dialog';
import { MultiSelectModule } from 'primeng/multiselect';

@NgModule({
    declarations: [ExternalUserSearchComponent, ExternalUserListComponent, ExternalUserFormComponent, ExternalUserDeleteDialogComponent],
    entryComponents: [ExternalUserFormComponent, ExternalUserDeleteDialogComponent],
    imports: [
        CommonModule,
        ExternalUserRoutingModule,
        ArteSpinnerModule,
        ArteEntityListEmptyModule,
        ArteTableModule,
        SharedModule,
        ArteDirectivesModule,
        ArteAutocompleteModule,
        ArteTriInputSwitchModule,
        ArteAuditInfoModule,
        FilterModule,
        DialogModule,
        MultiSelectModule,
        ArteCalendarModule,
    ],
    providers: [ExternalUserService],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class ExternalUserModule {}
