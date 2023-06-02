import { TypeExternalArtefacts } from '@app/shared/model/external-item.model';

interface LocalisedString {
    value: string;
    lang: string;
}

interface InternationalString {
    text:
        LocalisedString[];
}

export interface ItemCode {
    id: string;
    name: string;
}

export interface ExternalItemCodeResponse {
    id: string;
    name: InternationalString;
    kind: TypeExternalArtefacts;
}

export interface ExternalCodesResponse {
    code: ExternalItemCodeResponse[];
}
