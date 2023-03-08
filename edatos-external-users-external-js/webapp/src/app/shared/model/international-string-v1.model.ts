import { CURRENT_LANGUAGE } from '@app/core/service';
import { Type } from 'class-transformer';
import * as _ from 'lodash';
import { LocalisedStringV1 } from './localised-string-v1.model';

export class InternationalStringV1 {
    @Type(() => LocalisedStringV1)
    public text: LocalisedStringV1[] = [];

    public getLocalisedLabel(locale: string): string | undefined {
        return this.text.find((localisedString) => localisedString.locale === locale)?.label;
    }

    public getLocalisedString(locale: string): LocalisedStringV1 | undefined {
        return this.text.find((localisedString) => localisedString.locale === locale);
    }

    public isEmptyOrBlank(): boolean {
        return this.text.length === 0 || this.text.every((text) => text.label.trim().length === 0);
    }

    public has(locale: string): boolean {
        return !!this.getLocalisedString(locale);
    }

    public add(locale: string, label: string): void;
    public add(text: LocalisedStringV1): void;
    public add(textOrLocale: string | LocalisedStringV1, label?: string): void {
        if (textOrLocale instanceof LocalisedStringV1) {
            const localisedString = this.getLocalisedString(textOrLocale.locale);
            if (!localisedString) {
                this.text.push(textOrLocale);
            } else {
                localisedString.label = textOrLocale.label;
            }
        } else if (label) {
            const locale = textOrLocale;
            if (this.has(locale)) {
                this.getLocalisedString(locale)!.label = label;
            } else {
                this.text.push(new LocalisedStringV1(locale, label));
            }
        }
    }

    public remove(locale: string): void {
        _.remove(this.text, (text) => text.locale === locale);
    }

    public getLocales(): string[] {
        return this.text.map((text) => text.locale);
    }

    public getLabels(): string[] {
        return this.text.map((text) => text.label);
    }

    public get val(): string | undefined {
        return this.getLocalisedLabel(CURRENT_LANGUAGE);
    }
}
