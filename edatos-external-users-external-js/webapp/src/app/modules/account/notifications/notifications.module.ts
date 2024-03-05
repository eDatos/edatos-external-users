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
import { NotificationsRoutingModule } from './notifications-routing.module';
import { NotificationsComponent } from './notifications.component';
import { NoticeFilter } from '@app/modules/account/notifications/notifications-search/notice-search';
import { InputSwitchModule } from 'primeng';
import {
    NotificationsDetailComponent
} from '@app/modules/account/notifications/notifications-detail/notifications-detail.component';

@NgModule({
    imports: [
        SharedModule,
        NotificationsRoutingModule,
        NbDialogModule.forChild({ autoFocus: false }),
        ArteDirectivesModule,
        ArteAutocompleteModule,
        ArteTriInputSwitchModule,
        ArteAuditInfoModule,
        ArteSpinnerModule,
        ArteEntityListEmptyModule,
        CommonModule,
        ArteCalendarModule,
        InputSwitchModule
    ],
    providers: [NoticeFilter],
    declarations: [NotificationsComponent, NotificationsDetailComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class NotificationsModule {
}
