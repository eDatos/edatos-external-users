import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { convert } from '@app/core/utils/response-utils';
import { KeyAndPassword } from '@app/core/model/key-poassword.model';
import { ResponseUtils } from 'arte-ng/utils';

@Injectable()
export class ResetPasswordService {
    private resourceUrl = 'api/recover-password';

    constructor(private http: HttpClient) {}

    resetPassword(email: string): Observable<any> {
        return this.http.post(this.resourceUrl, email);
    }

    changePasswordReset(keyAndPassword: KeyAndPassword): Observable<String> {
        return this.http.post(`${this.resourceUrl}/change-password`, keyAndPassword, { responseType: 'text' });
    }

    existsUserWithKey(key: string): Observable<Boolean> {
        return this.http.get<boolean>(`${this.resourceUrl}/key-recovery/${key}`).pipe(
            map((res) => {
                return res.valueOf();
            })
        );
    }
}
