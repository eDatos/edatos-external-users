import { Component, EventEmitter, forwardRef, Input, Output } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { InternationalString, LocalisedString } from '@app/shared/model';
import * as _ from 'lodash';
import { TranslateService } from "@ngx-translate/core";

@Component({
    selector: 'app-multi-language-input',
    templateUrl: './multi-language-input.component.html',
    styleUrls: ['./multi-language-input.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            multi: true,
            useExisting: forwardRef(() => MultiLanguageInputComponent),
        },
    ],
})
export class MultiLanguageInputComponent implements ControlValueAccessor {
    /**
     * List of input fields to show according to the locale.
     */
    @Input()
    public locales: string[];

    @Input()
    public isEditMode = true;

    @Input()
    public defaultLocale: string;

    @Input()
    public inputType: 'text' | 'textarea' = 'text';


    @Output()
    public onEnterKeyDown: EventEmitter<InternationalString> = new EventEmitter();

    public isDisabled = false;
    public value = new InternationalString();
    public mappedValue: { [key: string]: string } = {};

    constructor(translateService: TranslateService) {
        this.value.texts = [];
    }

    public onTouched: any = () => {};
    public onChange: any = () => {};

    public isRequiredLocale(locale: string): boolean {
        if (!this.defaultLocale) {
            return true;
        }
        return locale === this.defaultLocale && this.isEditMode;
    }

    public writeValue(internationalString: InternationalString | null): void {
        // We check for null because this bug with Angular ngModel:
        // https://github.com/angular/angular/issues/14988.
        if (internationalString) {
            // The deep cloning is needed because when the ngModel is set to be one-way: we want to avoid
            // modifications to the object we received. To do that, we need to deeply clone it. Lodash handles
            // this for us.
            this.value = _.cloneDeep(internationalString);

            if (this.locales) {
                // If we receive locales, we need to check for those that are not in the international string and
                // create them.
                for (const locale of this.locales) {
                    this.value.add(internationalString.getLocalisedString(locale) || new LocalisedString(locale, ''));
                }
            } else {
                // If locales aren't passed, show those available in the international string.
                this.locales = internationalString.getLocales();
            }

            this.mappedValue = {};
            for (const val of this.value?.texts) {
                this.mappedValue[val.locale] = val.label;
            }
        }
    }

    public registerOnChange(fn: any): void {
        this.onChange = fn;
    }

    public registerOnTouched(fn: any): void {
        this.onTouched = fn;
    }

    public setDisabledState?(isDisabled: boolean): void {
        this.isDisabled = isDisabled;
    }

    public onEditorChange(event: any, locale: string) {
        for (const val of this.value.texts) {
            if (val.locale === locale) {
                val.label = event.currentTarget.value;
            }
        }
        this.mappedValue[locale] = event.currentTarget.value;
    }

    public onEditorTouched(event: any) {
        this.onTouched();
    }
}
