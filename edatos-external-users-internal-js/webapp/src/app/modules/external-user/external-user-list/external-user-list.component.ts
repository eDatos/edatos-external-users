import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ITEMS_PER_PAGE, PAGINATION_OPTIONS } from '@app/app.constants';
import { ExternalUser, User } from '@app/core/model';
import { PermissionService } from '@app/core/service/auth';
import { ExternalUserService } from '@app/core/service/user';
import { ResponseWrapper } from '@app/core/utils/response-utils';
import { ExternalUserFilter } from '@app/modules/external-user/external-user-search/external-user-filter';

import { ArteEventManager } from 'arte-ng/services';
import { LazyLoadEvent } from 'primeng/api';
import { Subscription } from 'rxjs';
import { ConfigService } from '@app/config';
import { ITableColumn } from 'arte-ng';

@Component({
    selector: 'app-external-user-list',
    templateUrl: './external-user-list.component.html',
    styleUrls: ['./external-user-list.component.scss'],
})
export class ExternalUserListComponent implements OnInit, OnDestroy {
    public totalItems: any;
    public itemsPerPage: any;
    public paginatorOptions = PAGINATION_OPTIONS;
    public page: any;
    public predicate: any;
    public reverse: any;
    public filters: ExternalUserFilter;
    public users: ExternalUser[];
    public simplifiedSignUp = false;
    public columns: ITableColumn[] = [
        {
            fieldName: 'name',
            sortable: true,
            header: {
                handler: 'translate',
                translatePath: 'externalUser.name',
            },
        },
        {
            fieldName: 'surname1',
            sortable: true,
            header: {
                handler: 'translate',
                translatePath: 'externalUser.surname1',
            },
        },
        {
            fieldName: 'surname2',
            sortable: true,
            header: {
                handler: 'translate',
                translatePath: 'externalUser.surname2',
            },
        },
        {
            fieldName: 'email',
            sortable: true,
            header: {
                handler: 'translate',
                translatePath: 'externalUser.email',
            },
        },
        {
            fieldName: 'language',
            sortable: true,
            header: {
                handler: 'translate',
                translatePath: 'externalUser.language',
            },
        },
    ];
    public simplifiedSignUpColumns: ITableColumn[] = [
        {
            fieldName: 'email',
            sortable: true,
            header: {
                handler: 'translate',
                translatePath: 'externalUser.email',
            },
        },
        {
            fieldName: 'language',
            sortable: true,
            header: {
                handler: 'translate',
                translatePath: 'externalUser.language',
            },
        },
    ];
    private eventSubscriber: Subscription;
    private searchSubscription: Subscription;

    constructor(
        public permissionService: PermissionService,
        private userService: ExternalUserService,
        private eventManager: ArteEventManager,
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private configService: ConfigService,
    ) {
        this.filters = new ExternalUserFilter();
        this.simplifiedSignUp = this.configService.getConfig().metadata.simplifiedSignUp;
    }

    public ngOnInit() {
        this.processUrlParams();
        this.loadAll();
        this.registerChangeInUsers();
    }

    public ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
        this.eventManager.destroy(this.searchSubscription);
    }

    public registerChangeInUsers() {
        this.eventSubscriber = this.eventManager.subscribe('userListModification', (response) => this.loadAll());
        this.searchSubscription = this.eventManager.subscribe('userSearch', (response) => {
            this.page = 1;
            const queryParams = Object.assign({}, this.filters.toUrl(this.activatedRoute.snapshot.queryParams));
            this.router.navigate([], { relativeTo: this.activatedRoute, queryParams });
            this.loadAll();
        });
    }

    public loadAll() {
        const transitionParams = {
            page: this.page - 1,
            size: this.size(),
            sort: this.sort(),
        };
        const queryRequestParams = {
            query: this.filters.toQuery(),
            ...transitionParams,
        };
        this.router.navigate(['/external-users'], { queryParams: this.filters.toUrl(this.activatedRoute.snapshot.queryParams) });
        this.userService.find(queryRequestParams).subscribe((res) => this.onSuccess(res));
    }

    public sort(): string[] {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
        }
        return result;
    }

    public loadData(e: LazyLoadEvent) {
        this.page = e.first! / e.rows! + 1;
        this.itemsPerPage = e.rows;
        if (e.sortField != null) {
            this.predicate = e.sortField;
            this.reverse = e.sortOrder === 1;
        }
        this.loadAll();
    }

    public isActivo(user: User): boolean {
        return !user.deletionDate;
    }

    private size(): number {
        return PAGINATION_OPTIONS.indexOf(Number(this.itemsPerPage)) > -1 ? this.itemsPerPage : ITEMS_PER_PAGE;
    }

    private onSuccess(data: ResponseWrapper<ExternalUser[]>) {
        this.totalItems = data.totalCount();
        this.users = data.body;
    }

    private processUrlParams(): void {
        this.activatedRoute.data.subscribe((data) => {
            this.page = data['pagingParams'].page;
            this.reverse = data['pagingParams'].ascending;
            this.predicate = data['pagingParams'].predicate;
            this.itemsPerPage = data['pagingParams'].itemsPerPage;
        });
        this.filters.processUrlParams(this.activatedRoute.snapshot.queryParams);
    }
}
