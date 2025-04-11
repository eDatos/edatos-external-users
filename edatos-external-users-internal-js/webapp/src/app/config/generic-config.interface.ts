export interface GenericConfig {
    cas: {
        login: any;
        logout: any;
        service: any;
    };
    endpoint: {
        appUrl: any;
        srmExternalEndpoint: string;
    };
    metadata: {
        simplifiedSignUp: boolean;
        navbarScriptUrl: string;
        language: string;
        defaultLanguage: string;
        nationalities: string;
        gender: string;
        nationalitiesRestriction: string;
        genderRestriction: string;
    };
}
