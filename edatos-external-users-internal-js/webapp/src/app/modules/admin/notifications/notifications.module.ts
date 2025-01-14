import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { NotificationsDetailComponent } from "@app/modules/admin/notifications/notifications-detail/notifications-detail.component";
import { NotificationsTableComponent } from "@app/modules/admin/notifications/notifications-table/notifications-table.component";
import { NgxSkeletonLoaderModule } from "ngx-skeleton-loader";
import { NotificationsComponent } from "@app/modules/admin/notifications/notifications.component";
import { TranslateModule } from "@ngx-translate/core";
import { SharedModule } from "@app/shared";
import { RouterModule } from "@angular/router";
import { NotificationsRoutingModule } from "@app/modules/admin/notifications/notifications.route";

@NgModule({
    declarations: [NotificationsComponent, NotificationsDetailComponent, NotificationsTableComponent],
    imports: [
        NotificationsRoutingModule,
        TranslateModule,
        NgxSkeletonLoaderModule,
        SharedModule,
        RouterModule
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class NotificationsModule {
}
