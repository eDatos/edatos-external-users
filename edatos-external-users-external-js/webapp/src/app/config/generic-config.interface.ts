export interface GenericConfig {
    endpoint: {
        appUrl: any;
        srmExternalEndpoint: string;
    };
    metamac: {
        visualizerPath: string;
    };
    metadata: {
        defaultLanguage: string;
        languageCookie: string;
        nationalities: string;
        gender: string;
    };
    template: {
        headerUrl: string;
        footerUrl: string;
    };
}
