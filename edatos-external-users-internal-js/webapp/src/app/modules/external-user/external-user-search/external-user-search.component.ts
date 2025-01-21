import { Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Language, Service} from '@app/core/model';
import { FavoriteResource } from '@app/shared/model';
import { CategoryService, LanguageService } from '@app/shared/service';
import { ArteEventManager } from 'arte-ng/services';
import * as _ from 'lodash';
import { of, Subject, Subscription } from 'rxjs';
import { debounceTime } from 'rxjs/operators';
import { ExternalUserFilter } from './external-user-filter';
import { TranslateService } from "@ngx-translate/core";
import { StructuralResourcesTreeSimpleSelectComponent } from "@app/shared/components/structural-resources-tree/structural-resources-tree-simple-select/structural-resources-tree-simple-select.component";

@Component({
    selector: 'app-external-user-search',
    templateUrl: 'external-user-search.component.html',
    styleUrls: ['external-user-search.component.scss'],
})
export class ExternalUserSearchComponent implements OnInit, OnDestroy {
    @Input()
    public filters: ExternalUserFilter;

    public subscription: Subscription;
    public internationalLanguages: Language[] = [];
    public selectedLanguage: Language[] = [];
    public selectedResources: FavoriteResource[] = [];
    public isSelectingResource = false;
    public serviceEnum = Service;

    @ViewChild(StructuralResourcesTreeSimpleSelectComponent)
    public tree: StructuralResourcesTreeSimpleSelectComponent;

    private filterChangesSubject: Subject<any> = new Subject<any>();

    constructor(private eventManager: ArteEventManager, public categoryService: CategoryService,  private translateService: TranslateService,
                private languageService: LanguageService) {}

    public ngOnInit() {
        this.subscription = this.filterChangesSubject.pipe(debounceTime(300)).subscribe(() =>
            this.eventManager.broadcast({
                name: 'userSearch',
                content: this.filters,
            })
        );
        this.categoryService.findAll({ size: 100000000 }).subscribe((response) => {
            this.selectedResources = response.body.filter((category) => this.filters.categories.includes(category.id.toString()));
            for (const category of response.body) {
                this.selectedResources.push(...category.externalOperations.filter((externalOp) => this.filters.externalOperations.includes(externalOp.id.toString())));
            }
        });

        this.getInternationalLanguages();
    }

    public ngOnDestroy() {
        this.eventManager.destroy(this.subscription);
    }

    public trackSelectedResource(index: number, item: FavoriteResource) {
        return `${item.id}${item.favoriteType}`;
    }

    public filter(): void {
        this.filterChangesSubject.next();
    }

    public resetFilters(): void {
        this.filters.reset();
        this.filter();
        this.resetCategories();
        this.resetLanguages();
    }

    public openCategoryModal() {
        this.isSelectingResource = true;
    }

    public closeCategoryModal() {
        this.isSelectingResource = false;
    }

    public selectCategories() {
        const categoriesIds: string[] = [];
        const externalOperationsIds: string[] = [];
        for (const resource of this.tree.selectedResources) {
            if (resource.data.favoriteType === 'category') {
                categoriesIds.push(resource.data.id.toString());
            } else {
                externalOperationsIds.push(resource.data.id.toString());
            }
        }
        this.filters.categories = categoriesIds;
        this.filters.externalOperations = externalOperationsIds;
        this.updateSelectedResources();
        this.closeCategoryModal();
        this.filter();
    }

    public onUnselectLanguages(language) {
        const index = this.selectedLanguage.indexOf(language);
        if (index > -1) {
            this.selectedLanguage.splice(index, 1);
        }
        this.filters.languages = this.selectedLanguage.map(language => language.locale);
        this.filter();
    }

    private resetLanguages() {
        this.selectedLanguage = [];
        this.filters.languages = this.selectedLanguage.map(language => language.locale);
        this.filter();
    }

    public onSelectLanguages(language) {
        this.selectedLanguage.push(language);
        this.filters.languages = this.selectedLanguage.map(language => language.locale);
        this.filter();
    }

    public resetCategories() {
        this.filters.categories = [];
        this.filters.externalOperations = [];
        this.selectedResources = [];
        this.filter();
    }

    private updateSelectedResources() {
        this.selectedResources = this.tree.selectedResources.map((node) => node.data);
    }

    public deleteResource(resource: FavoriteResource) {
        const filter = resource.favoriteType === 'category' ? this.filters.categories : this.filters.externalOperations;
        _.pull(this.selectedResources, resource);
        _.pull(filter, resource.id.toString());
        of(debounceTime(300)).subscribe(() => this.filter());
    }

    private getInternationalLanguages() {
        this.languageService.getInternationalLanguages().subscribe((response) => {
            this.internationalLanguages = response.map((lang) => {
                return new Language(lang, this.translateService.instant('enum.localeToLanguage.' + lang.toUpperCase()));
            });
            this.internationalLanguages.sort((a, b) => a.label.localeCompare(b.label));
            this.selectedLanguage = this.internationalLanguages.filter((language) => this.filters.languages.includes(language.locale));
        });
    }
}
