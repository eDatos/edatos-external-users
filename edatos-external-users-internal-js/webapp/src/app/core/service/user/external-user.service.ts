import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ExternalUser } from '@app/core/model';
import { convert, ResponseWrapper } from '@app/core/utils/response-utils';
import { createRequestOption } from 'arte-ng/utils';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable()
export class ExternalUserService {
    private resourceUrl = 'api/external-users';

    constructor(private http: HttpClient) {}

    public get(id: number, includeDeleted = true): Observable<ExternalUser> {
        const options = createRequestOption({ includeDeleted });
        return this.http.get<ExternalUser>(`${this.resourceUrl}/${id}`, options).pipe(map((res) => convert(ExternalUser, res)));
    }

    public find(req?: any): Observable<ResponseWrapper<ExternalUser[]>> {
        const options = createRequestOption(req);
        return this.http
            .get<ExternalUser[]>(this.resourceUrl, { ...options, observe: 'response' })
            .pipe(map((res) => convert(ExternalUser, res)));
    }

    public delete(id: string): Observable<ExternalUser> {
        return this.http.delete<ExternalUser>(`${this.resourceUrl}/${id}`).pipe(map((res) => convert(ExternalUser, res)));
    }
}
