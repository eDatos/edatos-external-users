import { CURRENT_LANGUAGE } from '@app/core/service';
import { Type } from 'class-transformer';
import * as _ from 'lodash';
import { LocalisedStringStructuralResources } from './localised-string-structural-resources.model';

export class InternationalStringStructuralResources {
    @Type(() => LocalisedStringStructuralResources)
    public text: LocalisedStringStructuralResources[] = [];

    public getLocalisedValue(lang: string): string | undefined {
        return this.text.find((localisedString) => localisedString.lang === lang)?.value;
    }

    public getLocalisedString(lang: string): LocalisedStringStructuralResources | undefined {
        return this.text.find((localisedString) => localisedString.lang === lang);
    }

    public isEmptyOrBlank(): boolean {
        return this.text.length === 0 || this.text.every((text) => text.value.trim().length === 0);
    }

    public has(lang: string): boolean {
        return !!this.getLocalisedString(lang);
    }

    public add(lang: string, value: string): void;
    public add(text: LocalisedStringStructuralResources): void;
    public add(textOrLang: string | LocalisedStringStructuralResources, value?: string): void {
        if (textOrLang instanceof LocalisedStringStructuralResources) {
            const localisedString = this.getLocalisedString(textOrLang.lang);
            if (!localisedString) {
                this.text.push(textOrLang);
            } else {
                localisedString.value = textOrLang.value;
            }
        } else if (value) {
            const lang = textOrLang;
            if (this.has(lang)) {
                this.getLocalisedString(lang)!.value = value;
            } else {
                this.text.push(new LocalisedStringStructuralResources(lang, value));
            }
        }
    }

    public remove(lang: string): void {
        _.remove(this.text, (text) => text.lang === lang);
    }

    public getLangs(): string[] {
        return this.text.map((text) => text.value);
    }

    public getValues(): string[] {
        return this.text.map((text) => text.value);
    }

    public get val(): string | undefined {
        return this.getLocalisedValue(CURRENT_LANGUAGE);
    }
}
