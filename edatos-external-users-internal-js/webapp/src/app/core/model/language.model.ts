/**
 * Languages supported by the external application, to be set by the user at registration.
 *
 * Note: The RAE Pan-Hispanic Dictionary indicates that "Castellano" is often used when
 * differentiating Spanish from the other languages of the state. It is listed here as
 * "Spanish" by convention and for internationalization reasons, but when displayed to
 * the user it should show "Castellano".
 */
export class Language {
    locale: string;
    label: string;

    constructor(locale: string, label: string) {
        this.locale = locale;
        this.label = label;
    }
}
