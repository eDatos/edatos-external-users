import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Filter } from '@app/core/model';
import { FilterFilter } from '@app/modules/filter/filter-search/filter-search';
import { FilterService } from '@app/shared/service/filter/filter.service';
import { ResponseWrapper } from '@app/core/utils/response-utils';
import { LazyLoadEvent } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'app-filter-list',
    templateUrl: './filter-list.component.html',
    styleUrls: ['filter-list.component.scss']
})
export class FilterListComponent implements OnInit {
    public virtualFilters: Filter[] = [];
    public filtersLoaded: boolean = false;
    public totalItems: number | null = null;
    public itemsPerLoad: number = 20;
    public rowHeight = 45;
    public columns: any = [
        {
            fieldName: 'name',
            sortable: true,
            width: '70%',
            header: {
                handler: 'translate',
                translatePath: 'filter.entity.name'
            }
        },
        {
            fieldName: 'createdDate',
            sortable: true,
            width: '25%',
            header: {
                handler: 'translate',
                translatePath: 'filter.entity.createdDate'
            }
        },
        {
            fieldName: 'lastAccessDate',
            sortable: true,
            width: '25%',
            header: {
                handler: 'translate',
                translatePath: 'filter.entity.lastAccessDate'
            }
        }
    ];
    private reverse?: boolean;
    private predicate: any;

    constructor(private filterService: FilterService, private activatedRoute: ActivatedRoute, private router: Router,
                private filterSearch: FilterFilter, private translateService: TranslateService) {}

    public ngOnInit(): void {
        const { pagingParams } = this.activatedRoute.snapshot.data;
        this.reverse = pagingParams.ascending;
        this.predicate = pagingParams.predicate;
    }

    private searchFilters(page: number) {
        const params = {
            page: page,
            size: this.itemsPerLoad,
            sort: this.sort(),
            query: this.filterSearch.toQuery()
        };

        const onComplete = () => this.filtersLoaded = true;
        this.filterService
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

    public transition(page: number) {
        const transitionParams = {
            sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
        };
        const queryParams = {
            ...this.filterSearch.toUrl(this.activatedRoute.snapshot.queryParams),
            ...transitionParams
        };
        this.router.navigate(['/filter'], { replaceUrl: true, queryParams });
        this.searchFilters(page);
    }

    public loadData(e: LazyLoadEvent) {
        const page = e.first / this.itemsPerLoad;
        if (e.sortField != null) {
            this.predicate = this.filterSearch.fromCamelCaseToSnakeCase(e.sortField);
            this.reverse = e.sortOrder === 1;
        }
        this.transition(page);
    }

    public sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
        }
        return result;
    }

    public onSuccess(response: ResponseWrapper<Filter[]>, page: number) {
        this.totalItems = response.totalCount();

        if (this.virtualFilters.length !== this.totalItems) {
            this.virtualFilters = Array.from({ length: this.totalItems });
        }

        const first = page * this.itemsPerLoad;
        this.virtualFilters.splice(first, this.itemsPerLoad, ...response.body);

        this.virtualFilters = [...this.virtualFilters];
    }
}
