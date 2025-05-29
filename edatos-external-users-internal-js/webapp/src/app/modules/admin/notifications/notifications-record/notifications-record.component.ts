import { Component, OnInit } from '@angular/core';
import { LazyLoadEvent } from 'primeng/api';
import { ResponseWrapper } from '@app/core/utils/response-utils';
import { TranslateService } from '@ngx-translate/core';
import { NotificationService } from "@app/shared/service";
import { NotificationRecord } from "@app/shared/model";
import { formatNotifications } from "@app/shared/utils/notificationUtils";
import { NotificationsFilter } from "@app/modules/admin/notifications/notifications-search/notifications-filter";
import { ActivatedRoute, Router } from "@angular/router";
import { Subscription } from "rxjs";
import { ArteEventManager } from "arte-ng/services";

@Component({
    selector: 'app-notifications-record',
    templateUrl: './notifications-record.component.html',
    styleUrls: ['./notifications-record.component.scss']
})
export class NotificationsRecordComponent implements OnInit {
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
            width: '40%',
            header: {
                handler: 'translate',
                translatePath: 'notifications.label.subject'
            }
        },
        {
            fieldName: 'receivers',
            sortable: false,
            width: '40%',
            header: {
                handler: 'translate',
                translatePath: 'notifications.label.receivers'
            }
        }
    ];
    public filters: NotificationsFilter;
    private searchSubscription?: Subscription;

    constructor(private notificationService: NotificationService, private translateService: TranslateService, private router: Router,
                private activatedRoute: ActivatedRoute, private eventManager: ArteEventManager) {
        this.filters = new NotificationsFilter();
    }

    public ngOnInit() {
        this.filters.processUrlParams(this.activatedRoute.snapshot.queryParams);
        this.registerChangeInNotifications();
    }

    searchNotifications(page: number): void {
        const params = {
            page: page,
            size: this.itemsPerLoad,
            query: this.filters.toQuery()
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

    public loadData(e?: LazyLoadEvent) {
        const page = (e?.first || 0) / this.itemsPerLoad;
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

    public registerChangeInNotifications() {
        this.searchSubscription = this.eventManager.subscribe('notificationSearch', () => {
            const queryParams = Object.assign({}, this.filters.toUrl(this.activatedRoute.snapshot.queryParams));
            this.router.navigate([], { relativeTo: this.activatedRoute, queryParams });
            this.loadData();
        });
    }
}
