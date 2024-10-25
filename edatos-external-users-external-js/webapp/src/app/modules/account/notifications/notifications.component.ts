import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LazyLoadEvent, MessageService } from 'primeng/api';
import { ResponseWrapper } from '@app/core/utils/response-utils';
import { NoticeFilter } from '@app/modules/account/notifications/notifications-search/notice-search';
import { NoticeService } from '@app/shared/service/user/notice.service';
import { Notice } from '@app/core/model/notice.model';
import { AccountUserService } from '@app/core/service';
import { User } from '@app/core/model';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'ac-account-management',
    templateUrl: './notifications.component.html',
    styleUrls: ['./notifications.component.scss']
})
export class NotificationsComponent implements OnInit {
    public virtualNotices: Notice[] = [];
    public noticesLoaded: boolean = false;
    public totalItems: number | null = null;
    public itemsPerLoad: number = 20;
    public rowHeight = 35;

    public markedEmailNotificationsEnabled = false;
    public account: User;

    public columns: any = [
        {
            fieldName: 'receivedDate',
            sortable: false,
            width: '20%',
            header: {
                handler: 'translate',
                translatePath: 'notices.label.receivedDate'
            }
        },
        {
            fieldName: 'subject',
            sortable: false,
            width: '80%',
            header: {
                handler: 'translate',
                translatePath: 'notices.label.subject'
            }
        }
    ];

    constructor(private noticeService: NoticeService, private activatedRoute: ActivatedRoute,
        private userService: AccountUserService, private messageService: MessageService,
        private translateService: TranslateService) {
    }

    ngOnInit(): void {
        this.account = this.activatedRoute.snapshot.data.user;
        this.markedEmailNotificationsEnabled = this.account.emailNotificationsEnabled;
    }

    searchNotices(page: number): void {
        const params = {
            page: page,
            size: this.itemsPerLoad
        };
        const onComplete = () => this.noticesLoaded = true;
        this.noticeService
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
    }

    public loadData(e: LazyLoadEvent) {
        const page = e.first / this.itemsPerLoad;
        this.searchNotices(page);
    }

    public onSuccess(response: ResponseWrapper<Notice[]>, page: number) {
        this.totalItems = response.totalCount();

        if (this.virtualNotices.length !== this.totalItems) {
            this.virtualNotices = Array.from({ length: this.totalItems });
        }

        const first = page * this.itemsPerLoad;
        this.virtualNotices.splice(first, this.itemsPerLoad, ...response.body);

        this.virtualNotices = [...this.virtualNotices];
    }

    public toggleVisibility() {
        this.userService.update(this.account).subscribe((response) => {
                this.account = response;
                this.markedEmailNotificationsEnabled = this.account.emailNotificationsEnabled;
                this.onUpdateSuccess();
            }
        );
    }

    private onUpdateSuccess() {
        this.messageService.add({
            key: 'customAlertKey',
            severity: 'success',
            summary: this.translateService.instant('account.messages.onSuccessSumary'),
            detail: this.translateService.instant('account.messages.onSuccess'),
            life: 5000
        });
    }

}
