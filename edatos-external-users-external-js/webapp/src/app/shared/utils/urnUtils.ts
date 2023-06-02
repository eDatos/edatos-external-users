import { ConfigService } from '@app/config';

export const TYPE_JSON = '.json?_type=json';

export function getIdentifierFromUrn(urn: string) {
    var splittedUrn = urn.split('=');
    var structuralResource = splittedUrn[1];

    var structuralResourceRegex = /(\w+):([a-z_0-9]+)\(([0-9\.]+)\)(\.([a-z_0-9]+))?/i;
    var structuralResourceMatchs = structuralResourceRegex.exec(structuralResource);

    return {
        agency: structuralResourceMatchs[1],
        identifier: structuralResourceMatchs[2],
        version: structuralResourceMatchs[3],
    };
}

export function getCodesUrlJSONType(configService: ConfigService, typeExternal: string, urn: string, query?: string, restrinction?: string, extended?: string): string {
    const type = 'codes' + TYPE_JSON;

    const url =
        configService.getConfig().endpoint.srmExternalEndpoint +
        typeExternal +
        '/' +
        getIdentifierFromUrn(urn).agency +
        '/' +
        getIdentifierFromUrn(urn).identifier +
        '/' +
        getIdentifierFromUrn(urn).version +
        '/' +
        type +
        restrinction +
        extended +
        query;

    return url;
}
