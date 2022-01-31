export interface GenericConfig {
    endpoint: {
        appUrl: any;
    };
    metamac: {
        visualizerPath: string;
    };
    metadata: {
        defaultLanguage: string;
        languageCookie: string;
    };
    template: {
        headerUrl: string;
        footerUrl: string;
    }
}
