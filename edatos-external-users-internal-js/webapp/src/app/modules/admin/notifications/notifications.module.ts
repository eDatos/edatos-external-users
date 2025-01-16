import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { NotificationsDetailComponent } from "@app/modules/admin/notifications/notifications-detail/notifications-detail.component";
import { NotificationsRecordComponent } from "@app/modules/admin/notifications/notifications-table/notifications-record.component";
import { NgxSkeletonLoaderModule } from "ngx-skeleton-loader";
import { NotificationsFormComponent } from "@app/modules/admin/notifications/notifications-form/notifications-form.component";
import { TranslateModule } from "@ngx-translate/core";
import { SharedModule } from "@app/shared";
import { RouterModule } from "@angular/router";
import { NotificationsRoutingModule } from "@app/modules/admin/notifications/notifications.route";
import { ArteAutocompleteModule } from "arte-ng";
import { DialogModule } from "primeng";
import { MultiLanguageInputModule } from "@app/shared/components/multi-language-input";

@NgModule({
    declarations: [NotificationsFormComponent, NotificationsDetailComponent, NotificationsRecordComponent],
    imports: [
        NotificationsRoutingModule,
        TranslateModule,
        NgxSkeletonLoaderModule,
        SharedModule,
        RouterModule,
        ArteAutocompleteModule,
        DialogModule,
        MultiLanguageInputModule
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class NotificationsModule {
}
