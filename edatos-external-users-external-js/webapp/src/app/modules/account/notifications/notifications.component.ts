import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LazyLoadEvent } from 'primeng/api';
import { ITEMS_PER_PAGE, PAGINATION_OPTIONS } from '@app/app.constants';
import { ResponseWrapper } from '@app/core/utils/response-utils';
import { NoticeFilter } from '@app/modules/account/notifications/notice-search/notice-search';
import { NoticeService } from '@app/shared/service/user/notice.service';
import { Messages, Notice } from '@app/core/model/notice.model';
import { throwError } from 'rxjs';

@Component({
    selector: 'ac-account-management',
    templateUrl: './notifications.component.html',
    styleUrls: ['./notifications.component.scss']
})
export class NotificationsComponent implements OnInit {
    public notices: Notice[] = [];
    public noticesLoaded: boolean = false;
    public totalItems: number | null = null;
    public paginatorOptions = PAGINATION_OPTIONS;
    public itemsPerPage?: number;

    public columns: any = [
        {
            fieldName: 'receivedDate',
            sortable: false,
            header: {
                handler: 'translate',
                translatePath: 'notices.entity.receivedDate'
            }
        },
        {
            fieldName: 'subject',
            sortable: false,
            header: {
                handler: 'translate',
                translatePath: 'notices.entity.subject'
            }
        },
        {
            fieldName: 'message',
            sortable: false,
            header: {
                handler: 'translate',
                translatePath: 'notices.entity.message'
            }
        }
    ];
    private page: any;
    private reverse?: boolean;
    private predicate: any;

    constructor(private noticeService: NoticeService, private activatedRoute: ActivatedRoute, private router: Router,
                private noticeSearch: NoticeFilter) {
        this.activatedRoute.data.subscribe((data) => {
            this.page = data['pagingParams'].page;
            this.reverse = data['pagingParams'].ascending;
            this.predicate = data['pagingParams'].predicate;
            this.itemsPerPage = data['pagingParams'].itemsPerPage;
        });
    }

    ngOnInit() {
        this.activatedRoute.queryParams.subscribe((params) => {
            this.noticeSearch.fromQueryParams(params).subscribe(() =>
                this.noticeService
                    .find({
                        page: this.page - 1,
                        size: PAGINATION_OPTIONS.indexOf(Number(this.itemsPerPage)) > -1 ? this.itemsPerPage : ITEMS_PER_PAGE,
                        query: this.noticeSearch.toQuery()
                    })
                    .subscribe(
                        (response) => this.onSuccess(response),
                        (error) => {
                            return throwError(error);
                        },
                        () => {
                            this.noticesLoaded = true;
                        }
                    )
            );
        });
    }

    public transition() {
        const transitionParams = {
            page: this.page,
            size: PAGINATION_OPTIONS.indexOf(Number(this.itemsPerPage)) > -1 ? this.itemsPerPage : ITEMS_PER_PAGE
        };
        const queryParams = {
            ...this.noticeSearch.toUrl(this.activatedRoute.snapshot.queryParams),
            ...transitionParams
        };
        this.router.navigate(['/notifications'], { replaceUrl: true, queryParams });
    }

    public loadData(e: LazyLoadEvent) {
        this.page = e.first! / e.rows! + 1;
        this.itemsPerPage = e.rows;
        this.transition();
    }


    public clear() {
        this.page = 0;
        this.router.navigate([
            '/notifications',
            {
                page: this.page
            }
        ]);
    }

    public onSuccess(response: ResponseWrapper<Notice[]>) {
        this.totalItems = response.totalCount();
        this.notices = response.body;
    }

    public joinMessagesText(messages: Messages): string {
        let text = '';
        for (const message of messages.messages) {
            text += message.text;
        }
        return text;
    }
}
