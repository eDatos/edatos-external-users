export interface GenericConfig {
    cas: {
        login: any;
        logout: any;
        service: any;
    };
    endpoint: {
        appUrl: any;
        srmExternal: string;
    };
    metadata: {
        navbarScriptUrl: string;
        defaultLanguage: string;
        nationalities: string;
        gender: string;
    };
}
