import { Component } from '@angular/core';
import { LazyLoadEvent } from 'primeng/api';
import { ResponseWrapper } from '@app/core/utils/response-utils';
import { TranslateService } from '@ngx-translate/core';
import { NotificationService } from "@app/shared/service";
import { NotificationRecord } from "@app/shared/model";
import { formatNotifications } from "@app/shared/utils/notificationUtils";

@Component({
    selector: 'app-notifications-record',
    templateUrl: './notifications-record.component.html',
    styleUrls: ['./notifications-record.component.scss']
})
export class NotificationsRecordComponent {
    public virtualNotifications: NotificationRecord[] = [];
    public notificationsLoaded: boolean = false;
    public totalItems: number = 0;
    public itemsPerLoad: number = 20;
    public rowHeight = 35;
    public columns: any = [
        {
            fieldName: 'receivedDate',
            sortable: false,
            width: '20%',
            header: {
                handler: 'translate',
                translatePath: 'notifications.label.receivedDate'
            }
        },
        {
            fieldName: 'subject',
            sortable: false,
            width: '80%',
            header: {
                handler: 'translate',
                translatePath: 'notifications.label.subject'
            }
        }
    ];

    constructor(private notificationService: NotificationService, private translateService: TranslateService) {
    }

    searchNotifications(page: number): void {
        const params = {
            page: page,
            size: this.itemsPerLoad
        };
        const onComplete = () => this.notificationsLoaded = true;
        this.notificationService
            .find(params)
            .subscribe({
                next: (responseWrapper) => this.onSuccess(responseWrapper, page),
                error: onComplete,
                complete: onComplete
            });
    }

    public printHeader(col: any): string {
        const header = col.header;
        if (typeof header === 'string') {
            return header;
        } else {
            if (header instanceof Function) {
                return header(col);
            } else if (header.handler === "translate") {
                return this.translateService.instant(header.translatePath);
            }
        }
        return "";
    }

    public loadData(e: LazyLoadEvent) {
        const page = (e.first || 0) / this.itemsPerLoad;
        this.searchNotifications(page);
    }

    public onSuccess(response: ResponseWrapper<NotificationRecord[]>, page: number) {
        this.totalItems = response.totalCount() || 0;

        if (this.virtualNotifications.length !== this.totalItems) {
            this.virtualNotifications = Array.from({ length: this.totalItems });
        }

        const first = page * this.itemsPerLoad;
        this.virtualNotifications.splice(first, this.itemsPerLoad, ...formatNotifications(response.body));

        this.virtualNotifications = [...this.virtualNotifications];
    }
}
