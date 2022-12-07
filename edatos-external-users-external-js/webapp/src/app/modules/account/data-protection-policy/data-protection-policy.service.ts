import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { DataProtectionPolicy } from '@app/shared/model/data-protection-policy.model';
import { convert } from '@app/core/utils/response-utils';

@Injectable()
export class DataProtectionPolicyService {

    private dataProtectionPolicyUrl = 'api/data-protection-policy';

    constructor(private http: HttpClient) { }

    getDataProtectionPolicy(): Observable<DataProtectionPolicy> {
        console.log("entra En normal")
        return this.http.get(this.dataProtectionPolicyUrl).pipe(map((response: any) => convert(DataProtectionPolicy, response)));
    }
    getDataUltimateProtectionPolicy(): Observable<DataProtectionPolicy> {
        console.log("entra En ultimate")
        return this.http.get(`${this.dataProtectionPolicyUrl}/ultimate`).pipe(map((response: any) => convert(DataProtectionPolicy, response)));
    }
}

