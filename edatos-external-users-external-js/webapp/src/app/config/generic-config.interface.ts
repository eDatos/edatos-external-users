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
        nationalitiesRestriction: string;
        genderRestriction: string;
    };
    template: {
        headerUrl: string;
        footerUrl: string;
    };
}
