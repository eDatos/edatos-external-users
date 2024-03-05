import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { convert, ResponseWrapper } from '@app/core/utils/response-utils';
import { createRequestOption } from 'arte-ng/utils';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Notice } from '@app/core/model/notice.model';

@Injectable({
    providedIn: 'root'
})
export class NoticeService {
    public readonly resourceUrl = 'api/notices';

    constructor(private http: HttpClient) {
    }

    public find(req?: any): Observable<ResponseWrapper<Notice[]>> {
        const options = createRequestOption(req);
        return this.http
            .get<Notice[]>(this.resourceUrl, { ...options, observe: 'response' })
            .pipe(map((res) => convert(Notice, res)));
    }

    public getNotice(urn: string): Observable<Notice> {
        return this.http.get<Notice>(`${this.resourceUrl}/${urn}`, { params: { urn: urn } }).pipe(map((notice) => convert(Notice, notice)));
    }

}
