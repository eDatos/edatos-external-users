import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { NotificationsDetailComponent } from "@app/modules/admin/notifications/notifications-detail/notifications-detail.component";
import { NotificationsTableComponent } from "@app/modules/admin/notifications/notifications-table/notifications-table.component";
import { NgxSkeletonLoaderModule } from "ngx-skeleton-loader";
import { NotificationsComponent } from "@app/modules/admin/notifications/notifications.component";
import { TranslateModule } from "@ngx-translate/core";
import { SharedModule } from "@app/shared";
import { RouterModule } from "@angular/router";
import { NotificationsRoutingModule } from "@app/modules/admin/notifications/notifications.route";
import { ArteAutocompleteModule } from "arte-ng";
import { DialogModule } from "primeng";
import { MultiLanguageInputModule } from "@app/shared/components/multi-language-input";

@NgModule({
    declarations: [NotificationsComponent, NotificationsDetailComponent, NotificationsTableComponent],
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
