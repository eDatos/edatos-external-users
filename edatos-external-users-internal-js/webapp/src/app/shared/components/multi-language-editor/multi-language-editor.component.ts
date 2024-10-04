import { Component, forwardRef, Input, OnChanges, QueryList, SimpleChanges, ViewChildren, OnInit } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { InternationalString, LocalisedString, RequiredLocalesEnum } from '@app/shared/model';
import { Editor } from 'primeng/editor';
import { TranslateService } from "@ngx-translate/core";
import * as _ from 'lodash';
import Quill from 'quill';

@Component({
    selector: 'app-multi-language-editor',
    templateUrl: './multi-language-editor.component.html',
    styleUrls: ['./multi-language-editor.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            multi: true,
            useExisting: forwardRef(() => MultiLanguageEditorComponent),
        }
    ]
})
export class MultiLanguageEditorComponent implements ControlValueAccessor, OnChanges, OnInit {

    onTouched: any = () => {};
    onChange: any = () => {};
    isDisabled: boolean = false;
    editors: any = {};
    value: InternationalString = new InternationalString();
    defaultLocale: string = "";

    @Input()
    locales: String[] = [];

    @Input()
    isEditMode: boolean = true;
    private translateService: TranslateService;

    @Input()
    requiredLocales: RequiredLocalesEnum;

    quillModules;

    @ViewChildren(Editor) set setEditors(editors: QueryList<Editor>) {
        if (editors) {
            editors.forEach(editor => {
                this.editors[editor.el.nativeElement.id] = editor.quill;
            });
        }
    }

    constructor(translateService: TranslateService) {
        this.translateService = translateService;
        this.value.texts = [];
        this.defaultLocale = translateService.getDefaultLang()
    }

    ngOnInit(): void {
        this.preventRelativeLinks();
        this.allowI18nForLinkTooltip();
    }

    private allowI18nForLinkTooltip() {
        const style = document.createElement('style');
        const insertUrlMessage = this.translateService.instant('richEditor.enterLink');
        const saveMessage = this.translateService.instant('richEditor.save');
        style.innerHTML = `.ql-snow .ql-tooltip[data-mode="link"]::before { content: "${insertUrlMessage}" !important; }\n`;
        style.innerHTML += `.ql-snow .ql-tooltip.ql-editing a.ql-action::after { content: "${saveMessage}" !important; }`;
        style.appendChild(document.createTextNode(''));
        document.head.appendChild(style);
    }

    private preventRelativeLinks() {
        const Link = Quill.import('formats/link');
        class MyLink extends Link {
          static create(value) {
            const node = super.create(value);
            if (!/^https?:\/\//i.test(value)) {
                value = `https://${value}`;
            }
            node.setAttribute('href', value);
            return node;
          }
        }

        Quill.register(MyLink, true);
        this.quillModules = this.isEditMode ? null : {
            toolbar: [
                ['link']
            ]
        };
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.locales) {
            this.locales.forEach(locale => {
                if (!this.value.texts.some(text => text.locale === locale)) {
                    let localisedString = new LocalisedString();
                    localisedString.locale = locale.toString();
                    localisedString.label = "";
                    this.value.texts.push(_.cloneDeep(localisedString));
                }
            });
        }
    }

    public isRequiredLocale(locale: string): boolean {
        if (this.requiredLocales === RequiredLocalesEnum.ALL_LOCALES) {
            return this.isEditMode;
        }
        if (this.requiredLocales === RequiredLocalesEnum.DEFAULT_LOCALE) {
            return locale === this.defaultLocale && this.isEditMode;
        }
        return false;
    }

    writeValue(value: InternationalString): void {
        this.value = _.cloneDeep(value) || new InternationalString();

        this.locales.forEach(locale => {
            let localisedString = this.value.texts.find(text => text.locale === locale);
            if (!localisedString) {
                localisedString = new LocalisedString();
                localisedString.locale = locale.toString();
                localisedString.label = "";
                this.value.texts.push(_.cloneDeep(localisedString));
            }
            this.editors[locale.toString()]?.setContents(this.editors[locale.toString()].clipboard.convert(localisedString.label));
        });
    }

    registerOnChange(fn: any): void {
        this.onChange = fn;
    }

    registerOnTouched(fn: any): void {
        this.onTouched = fn;
    }

    setDisabledState?(isDisabled: boolean): void {
        this.isDisabled = isDisabled;
        this.editors?.forEach(editor => {
            (this.isDisabled || !this.isEditMode) ? editor.disable() : editor.enable();
        });
    }

    onEditorInit(event: any, locale: string) {
        (this.isDisabled || !this.isEditMode) ? event.editor.disable() : event.editor.enable();
        event.editor.setContents(event.editor.clipboard.convert(this.value.getLocalisedLabel(locale) || ""));
    }

    onEditorChange(event: any, locale: string) {
        this.value.texts = this.value.texts.map(text => {
            if (text.locale === locale) text.label = event.htmlValue;
            return text;
        });
        this.onChange(this.value);
    }

    onEditorTouched(event: any) {
        this.onTouched();
    }
}
