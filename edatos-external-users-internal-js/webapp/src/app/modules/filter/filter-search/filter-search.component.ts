import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FilterFilter } from '@app/modules/filter';
import { ArteEventManager } from 'arte-ng/services';
import { Subject, Subscription } from 'rxjs';
import { debounceTime } from 'rxjs/operators';
import { ExternalOperation } from "@app/shared/model";
import { ExternalOperationService } from "@app/shared/service";
import { ConfigService } from '@app/config';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'app-filter-search',
    templateUrl: 'filter-search.component.html',
    styleUrls: ['filter-search.component.scss']
})
export class FilterSearchComponent implements OnInit, OnDestroy {
    @Input()
    public filters: FilterFilter;

    public subscription: Subscription;

    private filterChangesSubject: Subject<any> = new Subject<any>();

    public externalOperations: ExternalOperation[] = [];
    public selectedOperations: ExternalOperation[] = [];
    public simplifiedSignUp: boolean;

    constructor(private eventManager: ArteEventManager, private externalOperationService: ExternalOperationService, private configService: ConfigService, private translateService: TranslateService) {}

    public ngOnInit() {
        this.simplifiedSignUp = this.configService.getConfig().metadata.simplifiedSignUp;
        this.subscription = this.filterChangesSubject.pipe(debounceTime(300)).subscribe(() =>
            this.eventManager.broadcast({
                name: 'filterSearch',
                content: this.filters,
            })
        );

        this.externalOperationService.find({size: 10000}).subscribe((response) => {
            this.externalOperations = response.body.sort((operation1, operation2) => {
                return operation1.code.toLowerCase() < operation2.code.toLowerCase() ? -1 : 1;
            });
            this.selectedOperations = this.externalOperations.filter(operation => this.filters.externalOperations.includes(operation.id.toString()));
            this.filter();
        });
    }

    public ngOnDestroy() {
        this.eventManager.destroy(this.subscription);
    }

    public onSelectOperations(operation: ExternalOperation) {
        this.selectedOperations.push(operation);
        this.filters.externalOperations = this.selectedOperations.map(operation => operation.id.toString());
        this.filter();
    }

    public onUnselectOperations(operation: ExternalOperation) {
        const index = this.selectedOperations.indexOf(operation);
        if (index > -1) {
            this.selectedOperations.splice(index, 1);
        }
        this.filters.externalOperations = this.selectedOperations.map(operation => operation.id.toString());
        this.filter();
    }

    public filter(): void {
        this.filterChangesSubject.next();
    }

    public resetFilters(): void {
        this.filters.reset();
        this.selectedOperations = [];
        this.filter();
    }

    operationTemplate(operation: ExternalOperation): string {
        return operation.code + " - " + (operation.name.getLocalisedLabel(this.translateService.currentLang) || operation.name.texts[0].label);
    }
}
