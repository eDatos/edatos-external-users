import { CURRENT_LANGUAGE, DEFAULT_LANGUAGE } from '@app/core/service';
import { Type } from 'class-transformer';
import * as _ from 'lodash';
import { LocalisedString } from './localised-string.model';

export class InternationalString {
    @Type(() => LocalisedString)
    public texts: LocalisedString[] = [];

    public getLocalisedLabel(locale: string): string | undefined {
        return this.texts.find((localisedString) => localisedString.locale === locale)?.label;
    }

    public getLocalisedString(locale: string): LocalisedString | undefined {
        return this.texts.find((localisedString) => localisedString.locale === locale);
    }

    public isEmptyOrBlank(): boolean {
        return this.texts.length === 0 || this.texts.every((text) => text.label.trim().length === 0);
    }

    public has(locale: string): boolean {
        return !!this.getLocalisedString(locale);
    }

    public add(locale: string, label: string): void;
    public add(text: LocalisedString): void;
    public add(textOrLocale: string | LocalisedString, label?: string): void {
        if (textOrLocale instanceof LocalisedString) {
            const localisedString = this.getLocalisedString(textOrLocale.locale);
            if (!localisedString) {
                this.texts.push(textOrLocale);
            } else {
                localisedString.label = textOrLocale.label;
            }
        } else if (label) {
            const locale = textOrLocale;
            if (this.has(locale)) {
                this.getLocalisedString(locale)!.label = label;
            } else {
                this.texts.push(new LocalisedString(locale, label));
            }
        }
    }

    public remove(locale: string): void {
        _.remove(this.texts, (text) => text.locale === locale);
    }

    public getLocales(): string[] {
        return this.texts.map((text) => text.locale);
    }

    public getLabels(): string[] {
        return this.texts.map((text) => text.label);
    }

    public get val(): string | undefined {
        return this.getLocalisedLabel(CURRENT_LANGUAGE) || this.getLocalisedLabel(DEFAULT_LANGUAGE);
    }
}
