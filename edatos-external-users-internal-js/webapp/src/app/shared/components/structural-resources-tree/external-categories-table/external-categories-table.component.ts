import {Component, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {ExternalCategory} from '@app/shared/model';
import {CategoryService} from '@app/shared/service';
import {LazyLoadEvent} from 'primeng/api';
import {Table} from 'primeng/table';
import {Subject} from 'rxjs';
import {debounceTime, distinctUntilChanged, finalize} from 'rxjs/operators';

@Component({
    selector: 'app-external-categories-table',
    templateUrl: './external-categories-table.component.html',
    styleUrls: ['./external-categories-table.component.scss'],
})
export class ExternalCategoriesTableComponent implements OnInit, OnChanges {
    /**
     * The selected external categories.
     */
    @Input()
    public selectedExternalCategories: ExternalCategory[];

    /**
     * The external categories that must appear as disabled.
     */
    @Input()
    public disabledExternalCategories: ExternalCategory[];

    /**
     * List of external categories to be displayed in the table.
     */
    public externalCategories: ExternalCategory[] = [];

    @ViewChild(Table)
    public table: Table;

    public currentPageSelectedExternalCategories: ExternalCategory[];
    public totalItems: number;
    public loading = true;
    public globalSearch = new Subject<InputEvent>();

    constructor(public categoryService: CategoryService) {}

    public ngOnInit(): void {
        this.globalSearch.pipe(debounceTime(750), distinctUntilChanged()).subscribe((event: InputEvent) => {
            this.table.filterGlobal((event.target as HTMLInputElement).value, 'contains');
        });
    }

    public ngOnChanges(changes: SimpleChanges): void {
        if (changes.selectedExternalCategories) {
            this.currentPageSelectedExternalCategories = this.getSelectedResourcesOfTheCurrentPage();
        }
    }

    public getSelectedResources(): ExternalCategory[] {
        return this.selectedExternalCategories;
    }

    private getSelectedResourcesOfTheCurrentPage(): ExternalCategory[] {
        return this.selectedExternalCategories.filter(selectedCategory => {
            return this.externalCategories.some(category => selectedCategory.urn === category.urn);
        });
    }

    public isDisabled(resource: ExternalCategory): boolean {
        return this.disabledExternalCategories.some((category) => category.urn === resource.urn);
    }

    public onToggleAll() {
        if (this.currentPageSelectedExternalCategories.length > 0) {
            this.onSelectAll();
        } else {
            this.onUnselectAll();
        }
    }

    private onSelectAll() {
        this.currentPageSelectedExternalCategories = this.currentPageSelectedExternalCategories.filter((externalCat) => !this.isDisabled(externalCat));
        this.selectedExternalCategories = this.selectedExternalCategories.concat(this.currentPageSelectedExternalCategories.filter(currentPageSelectedCategory => {
            return !this.selectedExternalCategories.some(selectedCategory => currentPageSelectedCategory.urn === selectedCategory.urn);
        }));
    }

    private onUnselectAll() {
        this.selectedExternalCategories = this.selectedExternalCategories.filter(selectedCategory => {
            return !this.externalCategories.some(category => selectedCategory.urn === category.urn);
        });
    }

    public onRowSelect(event) {
        this.selectedExternalCategories = this.selectedExternalCategories.concat(event.data);
    }

    public onRowUnselect(event) {
        this.selectedExternalCategories = this.selectedExternalCategories.filter(selectedCategory => selectedCategory.urn !== event.data.urn);
    }

    public lazyLoad(e: LazyLoadEvent): void {
        const request = {
            page: e.first! / e.rows!,
            size: e.rows,
        };
        if (e.globalFilter) {
            Object.assign(request, { search: e.globalFilter });
        }
        if (e.sortField) {
            Object.assign(request, { sort: `${e.sortField},${e.sortOrder === 1 ? 'ASC' : 'DESC'}` });
        }
        this.update(request);
    }

    public update(params?): void {
        this.loading = true;
        this.categoryService
            .getExternalCategories(params)
            .pipe(finalize(() => (this.loading = false)))
            .subscribe((categories) => {
                this.totalItems = categories.totalCount()!;
                this.externalCategories = categories.body;
                this.currentPageSelectedExternalCategories = this.getSelectedResourcesOfTheCurrentPage();
            });
    }
}
