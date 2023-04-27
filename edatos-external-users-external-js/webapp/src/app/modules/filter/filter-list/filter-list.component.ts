import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ITEMS_PER_PAGE, PAGINATION_OPTIONS } from '@app/app.constants';
import { Filter } from '@app/core/model';
import { FilterFilter } from '@app/modules/filter/filter-search/filter-search';
import { FilterService } from '@app/shared/service/filter/filter.service';
import { ResponseWrapper } from '@app/core/utils/response-utils';
import { LazyLoadEvent } from 'primeng/api';

@Component({
    selector: 'app-filter-list',
    templateUrl: './filter-list.component.html',
    styleUrls: ['filter-list.component.scss'],
})
export class FilterListComponent implements OnInit {
    public filters: Filter[] = [];
    public filtersLoaded: boolean = false;
    public totalItems: number | null = null;
    public paginatorOptions = PAGINATION_OPTIONS;
    public itemsPerPage?: number;
    public columns: any = [
        {
            fieldName: 'name',
            sortable: true,
            header: {
                handler: 'translate',
                translatePath: 'filter.entity.name',
            },
        },
        {
            fieldName: 'createdDate',
            sortable: true,
            header: {
                handler: 'translate',
                translatePath: 'filter.entity.createdDate',
            },
        },
        {
            fieldName: 'lastAccessDate',
            sortable: true,
            header: {
                handler: 'translate',
                translatePath: 'filter.entity.lastAccessDate',
            },
        },
    ];
    private page: any;
    private reverse?: boolean;
    private predicate: any;

    constructor(private filterService: FilterService, private activatedRoute: ActivatedRoute, private router: Router, private filterSearch: FilterFilter) {
        this.activatedRoute.data.subscribe((data) => {
            this.page = data['pagingParams'].page;
            this.reverse = data['pagingParams'].ascending;
            this.predicate = data['pagingParams'].predicate;
            this.itemsPerPage = data['pagingParams'].itemsPerPage;
        });
    }

    public ngOnInit(): void {
        this.activatedRoute.queryParams.subscribe((params) => {
            this.filterSearch.fromQueryParams(params).subscribe(() =>
                this.filterService
                    .find({
                        page: this.page - 1,
                        size: PAGINATION_OPTIONS.indexOf(Number(this.itemsPerPage)) > -1 ? this.itemsPerPage : ITEMS_PER_PAGE,
                        sort: this.sort(),
                        query: this.filterSearch.toQuery(),
                    })
                    .subscribe(
                        (rw) => this.onSuccess(rw),
                        () => {}, 
                        () => { this.filtersLoaded = true; }
                    )
            );
        });
    }

    public transition() {
        const transitionParams = {
            page: this.page,
            size: PAGINATION_OPTIONS.indexOf(Number(this.itemsPerPage)) > -1 ? this.itemsPerPage : ITEMS_PER_PAGE,
            sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc'),
        };
        const queryParams = {
            ...this.filterSearch.toUrl(this.activatedRoute.snapshot.queryParams),
            ...transitionParams,
        };
        this.router.navigate(['/filter'], { replaceUrl: true, queryParams });
    }

    public loadData(e: LazyLoadEvent) {
        this.page = e.first! / e.rows! + 1;
        this.itemsPerPage = e.rows;
        if (e.sortField != null) {
            this.predicate = this.filterSearch.fromCamelCaseToSnakeCase(e.sortField);
            this.reverse = e.sortOrder === 1;
        }
        this.transition();
    }

    public sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
        }
        return result;
    }

    public clear() {
        this.page = 0;
        this.router.navigate([
            '/filter',
            {
                page: this.page,
                sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc'),
            },
        ]);
    }

    public onSuccess(response: ResponseWrapper<Filter[]>) {
        this.totalItems = response.totalCount();
        this.filters = response.body;
    }
}
