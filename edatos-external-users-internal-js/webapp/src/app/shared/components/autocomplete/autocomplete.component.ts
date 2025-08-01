import { Component, EventEmitter, forwardRef, HostBinding, Input, OnInit, Output, ViewChild } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { ApplicationUtils } from 'arte-ng/utils';
import { AutoComplete } from 'primeng/autocomplete';

export const AC_AUTOCOMPLETE_VALUE_ACCESSOR: any = {
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => AutocompleteComponent),
    multi: true
};

const ITEM_TEMPLATE_FIELD = '_ITEM_TEMPLATE_FIELD_';

// TODO: INFRASTR-113 - Revisar la implementación del componente order-list
@Component({
    selector: 'app-autocomplete',
    templateUrl: 'autocomplete.component.html',
    providers: [AC_AUTOCOMPLETE_VALUE_ACCESSOR]
})
export class AutocompleteComponent implements ControlValueAccessor, OnInit {
    // Atributos internos
    @ViewChild(AutoComplete)
    private autoComplete: AutoComplete;

    private internalProperties: string[] = [];

    public debouncedMode;

    protected _selectedSuggestions: any;

    protected _suggestions: any[];

    public filteredSuggestions: any[];

    public field: string = '';

    private myNewLabel = '';

    public internalItemTemplate: Function;

    // Parametros opcionales
    @Output()
    private onClear: EventEmitter<any> = new EventEmitter();

    @Output()
    private onBlur: EventEmitter<any> = new EventEmitter();

    @Output()
    private onSelect: EventEmitter<any> = new EventEmitter();

    @Output()
    private onUnselect: EventEmitter<any> = new EventEmitter();

    @Input()
    public createNonFound = false;

    @Input()
    public deleteOnBackspace = true;

    @Input()
    public inputId: string;

    @Input()
    public dataKey = 'id';

    @Input()
    public emptyMessage = this.translateService.instant('arte.autocomplete.emptyMessage');

    public placeholder = this.translateService.instant('arte.autocomplete.placeholder');

    @Input()
    public minLength = 0;

    @Input()
    public required = false;

    @Input()
    @HostBinding('class.disabled')
    public disabled = false;

    @Input()
    public multiple = false;

    @Input()
    public itemTemplate: Function;

    // Parametros obligatorios
    @Input()
    public properties: string[];

    @Output()
    public completeMethod: EventEmitter<any> = new EventEmitter();

    constructor(protected translateService: TranslateService) {}

    ngOnInit() {
        if (this.itemTemplate === undefined && (this.properties === undefined || this.properties.length === 0)) {
            throw new Error('properties is required');
        }

        if (this.itemTemplate !== undefined && this.properties !== undefined && this.properties.length > 0) {
            throw new Error('itemTemplate override properties! You must delete properties');
        }

        if (this.createNonFound && this.properties !== undefined && this.properties.length > 1) {
            throw new Error('is not possible to create new elements if several fields are showed');
        }

        if (this.createNonFound && this.itemTemplate !== undefined) {
            throw new Error('is not possible to create new elements if there is a custom item template');
        }

        this.initFieldAndPropertiesAndItemTemplate();
        this.debouncedMode = this.completeMethod.observers.length > 0;
    }

    private initFieldAndPropertiesAndItemTemplate() {
        if (this.itemTemplate !== undefined) {
            this.internalItemTemplate = this.itemTemplate;
        } else {
            this.internalItemTemplate = this.defaultItemTemplate;
        }

        if (this.properties !== undefined) {
            this.internalProperties = this.properties;

            if (this.properties.length === 1) {
                this.field = this.properties[0];
            }
        }

        if (this.isWrapCase()) {
            this.field = ITEM_TEMPLATE_FIELD;
            this.internalProperties = [this.field];
        }
    }

    protected onModelChange: Function = () => {};

    private onModelTouched: Function = () => {};

    private defaultItemTemplate(item) {
        return this.properties.map((property) => item[property]).join(' ');
    }

    private compareWith(selectedSuggestion, existingSuggestion) {
        if (selectedSuggestion && existingSuggestion) {
            if (!selectedSuggestion[this.dataKey]) {
                console.error(`selectedSuggestion don't have defined field ${this.dataKey}`, selectedSuggestion);
            }
            if (!existingSuggestion[this.dataKey]) {
                console.error(`existingSuggestion don't have defined field ${this.dataKey}`, existingSuggestion);
            }
            return selectedSuggestion[this.dataKey] === existingSuggestion[this.dataKey];
        } else {
            return selectedSuggestion === existingSuggestion;
        }
    }

    onCompleteMethod($event) {
        this.completeMethod.emit($event);
        // Aquí pasamos la query explicitamente para evitar interacción con el
        // dropdownMode (véase https://github.com/primefaces/primeng/blob/4.3.0/src/app/components/autocomplete/autocomplete.ts#L393)
        this.updateFilteredSuggestions($event.query);
    }

    itsNewSuggestion(item) {
        return this.myNewLabel === item[this.field] ? this.translateService.instant('arte.autocomplete.createNewItem') : '';
    }

    onSelectMethod($event) {
        this.onSelect.emit($event);
    }

    onUnselectMethod($event) {
        this.onUnselect.emit($event);
    }

    onClearMethod($event) {
        this.onClear.emit($event);
    }

    private isWrapCase(): boolean {
        return !this.multiple && ((this.properties !== undefined && this.properties.length > 1) || this.itemTemplate !== undefined);
    }

    private updateFilteredSuggestions(query?: string) {
        query = query !== undefined && query !== null ? query : this.getQueryValue();

        let updatedFilteredSuggestions = this.suggestions ? this.suggestions.slice() : [];

        if (this.isWrapCase()) {
            updatedFilteredSuggestions = this.wrapItemList(updatedFilteredSuggestions);
        }

        if (this._selectedSuggestions instanceof Array) {
            updatedFilteredSuggestions = this.excludeAlreadySelectedSuggestions(updatedFilteredSuggestions);
        }

        // TODO: INFRASTR-113 - Revisar la implementación del componente order-list
        if (this.internalProperties !== undefined && this.internalProperties.length > 0) {
            updatedFilteredSuggestions = this.filterByProperties(updatedFilteredSuggestions, query);
        }

        if (this.createNonFound) {
            updatedFilteredSuggestions = this.addNonFound(updatedFilteredSuggestions, query);
        }

        this.filteredSuggestions = updatedFilteredSuggestions;
    }

    private wrapItemList(suggestions: any[]) {
        if (this.internalItemTemplate !== undefined) {
            return suggestions.map((suggestion) => {
                return this.wrapItem(suggestion);
            });
        } else {
            return suggestions;
        }
    }

    private wrapItem(item) {
        if (item) {
            return Object.assign({}, item, { [ITEM_TEMPLATE_FIELD]: this.internalItemTemplate(item) });
        } else {
            return item;
        }
    }

    private addNonFound(filteredSuggestions, query) {
        this.myNewLabel = '';
        if (query && filteredSuggestions && !filteredSuggestions.some((suggestion) => suggestion[this.field].toLowerCase() === query.toLowerCase())) {
            this.myNewLabel = query;
            filteredSuggestions.push(this.buildNewSuggestion(query));
        }
        return filteredSuggestions;
    }

    private buildNewSuggestion(fieldValue) {
        const newSuggestion = {};
        newSuggestion[this.field] = fieldValue;
        return newSuggestion;
    }

    private filterByProperties(suggestions: any[], query: string) {
        return suggestions.filter((suggestion) => {
            // TODO: INFRASTR-113 - Revisar la implementación del componente order-list
            if (this.internalProperties.length > 0) {
                return (
                    this.internalProperties.findIndex((property) => {
                        return this.queryContainsValue(query, suggestion[property]);
                    }) !== -1
                );
            } else {
                return this.queryContainsValue(query, suggestion);
            }
        });
    }

    private queryContainsValue(query: string, value: string): boolean {
        if (!value) {
            return false;
        }
        return ApplicationUtils.removeDiacritics(value.toUpperCase()).indexOf(ApplicationUtils.removeDiacritics(query.toUpperCase())) !== -1;
    }

    private excludeAlreadySelectedSuggestions(suggestions: any[]) {
        return suggestions.filter((suggestion) => {
            const self = this;
            return !self._selectedSuggestions || self._selectedSuggestions.findIndex((selectedSuggestion) => self.compareWith(selectedSuggestion, suggestion)) === -1;
        });
    }

    handleOnFocusOutSuggestions($event) {
        this.onModelTouched();
        this.onBlur.emit($event);
    }

    private getQueryValue(): string {
        if (this.multiple) {
            return this.autoComplete.multiInputEL ? this.autoComplete.multiInputEL.nativeElement.value : '';
        } else {
            return this.autoComplete.inputEL ? this.autoComplete.inputEL.nativeElement.value : '';
        }
    }

    @Input()
    set suggestions(suggestions: any[]) {
        this._suggestions = suggestions;
        if (this.autoComplete != null) {
            this.updateFilteredSuggestions();
        }
    }

    get suggestions(): any[] {
        return this._suggestions;
    }

    /* ControlValueAccessor */
    writeValue(value: any): void {
        if (this.isWrapCase()) {
            this._selectedSuggestions = this.wrapItem(value);
        } else {
            this._selectedSuggestions = value;
        }
    }

    @Input()
    get selectedSuggestions(): any {
        return this._selectedSuggestions;
    }

    set selectedSuggestions(value) {
        this._selectedSuggestions = value;
        this.onModelChange(this._selectedSuggestions);
    }

    registerOnChange(fn: Function): void {
        this.onModelChange = fn;
    }

    registerOnTouched(fn: Function): void {
        this.onModelTouched = fn;
    }
}
