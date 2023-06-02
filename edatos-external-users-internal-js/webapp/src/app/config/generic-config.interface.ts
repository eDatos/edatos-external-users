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
        navbarScriptUrl: string;
        defaultLanguage: string;
        nationalities: string;
        gender: string;
    };
}
