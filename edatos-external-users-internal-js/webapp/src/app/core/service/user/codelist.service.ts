import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ConfigService } from '@app/config';
import { ExternalCodesResponse, ItemCode } from '@app/shared/model/external-item-code-response.model';
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

    public findAllCountries(): Observable<ItemCode[]> {
        const restriction = '&restriction=PAISES';
        const extended = '&extended=false';
        const url = getCodesUrlJSONType(this.configService, TYPE_EXTERNAL_CODELISTS, this.configService.getConfig().metadata.nationalities, '', restriction, extended);
        return this.getAllItemCodes(url);
    }

    public findNationality(code: string): Observable<ItemCode> {
        const query = "&query=ID%20EQ%20'" + code + "'";
        const url = getCodesUrlJSONType(this.configService, TYPE_EXTERNAL_CODELISTS, this.configService.getConfig().metadata.nationalities, query, '', '');
        return this.getOneItemCode(url);
    }

    public findAllGenders(): Observable<ItemCode[]> {
        const restriction = '&restriction=SEXO';
        const extended = '&extended=false';
        const url = getCodesUrlJSONType(this.configService, TYPE_EXTERNAL_CODELISTS, this.configService.getConfig().metadata.gender, '', restriction, extended);
        return this.getAllItemCodes(url);
    }

    public findGender(code: string): Observable<ItemCode> {
        const query = "&query=ID%20EQ%20'" + code + "'";
        const url = getCodesUrlJSONType(this.configService, TYPE_EXTERNAL_CODELISTS, this.configService.getConfig().metadata.gender, query, '', '');
        return this.getOneItemCode(url);
    }

    private getAllItemCodes(url: string) {
        return this.http.get<ExternalCodesResponse>(url, { headers: { 'X-Public-Request': '1' } }).pipe(map((res) => {
            return res.code.map((item) => {
                return { id: item.id, name: this.getValue(item.name.text) };
            });
        }));
    }

    private getOneItemCode(url: string) {
        return this.http.get<ExternalCodesResponse>(url, { headers: { 'X-Public-Request': '1' } }).pipe(map((res) => {
            return { id: res.code[0].id, name: this.getValue(res.code[0].name.text) };
        }));
    }

    private getValue(texts: { lang: string, value: string }[]): string {
        var value = texts.find((ll) => ll.lang == this.translateService.currentLang)?.value;
        if (!value) {
            value = texts.find((ll) => ll.lang == this.translateService.defaultLang)?.value;
        }
        return value || "";

    }
}
