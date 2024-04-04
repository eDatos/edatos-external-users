import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { convert, ResponseWrapper } from '@app/core/utils/response-utils';
import { Filter } from '@app/shared/model';
import { createRequestOption } from 'arte-ng/utils';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class FilterService {
    public readonly resourceUrl = 'api/filters';

    constructor(private http: HttpClient) {}

    public get(id: number): Observable<Filter> {
        return this.http.get<Filter>(`${this.resourceUrl}/${id}`).pipe(map((res) => convert(Filter, res)));
    }

    public find(req?: any): Observable<ResponseWrapper<Filter[]>> {
        const options = createRequestOption(req);
        return this.http
            .get<Filter[]>(this.resourceUrl, { ...options, observe: 'response' })
            .pipe(map((res) => convert(Filter, res)));
    }
}
