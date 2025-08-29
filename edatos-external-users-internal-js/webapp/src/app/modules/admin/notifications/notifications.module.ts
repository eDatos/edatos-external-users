import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { NotificationsDetailComponent } from "@app/modules/admin/notifications/notifications-detail/notifications-detail.component";
import { NotificationsRecordComponent } from "@app/modules/admin/notifications/notifications-record/notifications-record.component";
import { NgxSkeletonLoaderModule } from "ngx-skeleton-loader";
import { NotificationsFormComponent } from "@app/modules/admin/notifications/notifications-form/notifications-form.component";
import { TranslateModule } from "@ngx-translate/core";
import { SharedModule } from "@app/shared";
import { RouterModule } from "@angular/router";
import { NotificationsRoutingModule } from "@app/modules/admin/notifications/notifications.route";
import {ArteAutocompleteModule, ArteTriInputSwitchModule} from "arte-ng";
import { DialogModule } from "primeng";
import { MultiLanguageInputModule } from "@app/shared/components/multi-language-input";
import { NotificationsSearchComponent } from './notifications-search/notifications-search.component';

@NgModule({
    declarations: [NotificationsFormComponent, NotificationsDetailComponent, NotificationsRecordComponent, NotificationsSearchComponent],
    imports: [
        NotificationsRoutingModule,
        TranslateModule,
        NgxSkeletonLoaderModule,
        SharedModule,
        RouterModule,
        ArteAutocompleteModule,
        DialogModule,
        MultiLanguageInputModule,
        ArteTriInputSwitchModule,
        SharedModule
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class NotificationsModule {
}
