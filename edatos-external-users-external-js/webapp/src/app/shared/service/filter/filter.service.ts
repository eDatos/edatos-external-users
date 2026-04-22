import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Filter } from '@app/shared/model';
import { convert, ResponseWrapper } from '@app/core/utils/response-utils';
import { createRequestOption, ResponseUtils } from 'arte-ng/utils';
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

    public save(filter: Filter): Observable<Filter> {
        return this.http.post<Filter>(this.resourceUrl, filter).pipe(map((res) => convert(Filter, res)));
    }

    public update(filter: Filter): Observable<Filter> {
        return this.http.put<Filter>(this.resourceUrl, filter).pipe(map((res) => convert(Filter, res)));
    }

    public delete(id: number): Observable<ResponseWrapper<Filter>> {
        return this.http
            .delete<Filter>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res) => convert(Filter, res)));
    }
}
