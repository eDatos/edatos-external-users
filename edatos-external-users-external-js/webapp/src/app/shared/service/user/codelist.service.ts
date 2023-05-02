import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ConfigService } from '@app/config';
import { convert } from '@app/core/utils/response-utils';
import { ExternalCodes } from '@app/shared/model';
import { getCodesUrlJSONType } from '@app/shared/utils/urnUtils';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export const TYPE_EXTERNAL_CODELISTS = 'codelists';

@Injectable({
    providedIn: 'root',
})
export class CodelistService {
    constructor(private http: HttpClient, private configService: ConfigService, private translateService: TranslateService) {}

    public findAllCountries(): Observable<ExternalCodes> {
        const restriction = '&restriction=PAISES';
        const extended = '&extended=false';
        const url = getCodesUrlJSONType(this.configService, TYPE_EXTERNAL_CODELISTS, this.configService.getConfig().metadata.nationalities, '', restriction, extended);
        return this.http.get<ExternalCodes>(url, {headers:{"X-Public-Request":"1"}}).pipe(map((res) => convert(ExternalCodes, res)));
    }

    public findNationality(code: string): Observable<ExternalCodes> {
        const query = "&query=ID%20EQ%20'" + code + "'";
        const url = getCodesUrlJSONType(this.configService, TYPE_EXTERNAL_CODELISTS, this.configService.getConfig().metadata.nationalities, query, '', '');
        return this.http.get<ExternalCodes>(url, {headers:{"X-Public-Request":"1"}}).pipe(map((res) => convert(ExternalCodes, res)));
    }

    public findAllGenders(): Observable<ExternalCodes> {
        const restriction = '&restriction=SEXO';
        const extended = '&extended=false';
        const url = getCodesUrlJSONType(this.configService, TYPE_EXTERNAL_CODELISTS, this.configService.getConfig().metadata.gender, '', restriction, extended);
        return this.http.get<ExternalCodes>(url, {headers:{"X-Public-Request":"1"}}).pipe(map((res) => convert(ExternalCodes, res)));
    }

    public findGender(code: string): Observable<ExternalCodes> {
        const query = "&query=ID%20EQ%20'" + code + "'";
        const url = getCodesUrlJSONType(this.configService, TYPE_EXTERNAL_CODELISTS, this.configService.getConfig().metadata.gender, query, '', '');
        return this.http.get<ExternalCodes>(url, {headers:{"X-Public-Request":"1"}}).pipe(map((res) => convert(ExternalCodes, res)));
    }

    public getValue(texts: any[]): string {
        let val = null;
        for (const ll of texts) {
            if (this.translateService.currentLang == ll.lang) {
                val = ll.value;
            }
        }
        return val;
    }
}
