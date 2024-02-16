export class Language {
    locale: string;
    value: string;

    constructor(key: string, value: string) {
        this.locale = key;
        this.value = value;
    }
}
