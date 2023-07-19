export let DEFAULT_LANGUAGE = '';
export let CURRENT_LANGUAGE = '';

export function setCurrentLanguage(lang: string): void {
    CURRENT_LANGUAGE = lang;
}

export function setDefaultLanguage(lang: string): void {
    DEFAULT_LANGUAGE = lang;
}
