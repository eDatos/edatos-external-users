/*
    Languages codes are ISO_639-1 codes, see http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
    They are written in English to avoid character encoding issues (not a perfect solution)
*/
export const LANGUAGES: string[] = ['es', 'ca'];

export let DEFAULT_LANGUAGE = '';
export let CURRENT_LANGUAGE = '';

export function setCurrentLanguage(lang: string): void {
    CURRENT_LANGUAGE = lang;
}

export function setDefaultLanguage(lang: string): void {
    DEFAULT_LANGUAGE = lang;
}
