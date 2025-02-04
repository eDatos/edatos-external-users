import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Notification, NotificationRecord, Receiver } from '@app/shared/model';
import { convert, ResponseWrapper } from '@app/core/utils/response-utils';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { createRequestOption } from "arte-ng/utils";

@Injectable({
    providedIn: 'root',
})
export class NotificationService {
    public readonly resourceUrl = 'api/notifications';
    public readonly resourceReceiverUrl = 'api/receivers';

    constructor(private http: HttpClient) {}

    public send(notification: Notification): Observable<Notification> {
        return this.http.post<Notification>(this.resourceUrl, notification).pipe(map((notice) => convert(Notification, notice)));
    }

    public findReceivers(): Observable<Receiver[]> {
        return this.http.get<Receiver[]>(this.resourceReceiverUrl).pipe(map((res) => convert(Receiver, res)));
    }

    public getNotification(urn: string): Observable<NotificationRecord> {
        return this.http
            .get<NotificationRecord>(`${this.resourceUrl}/${urn}`, { params: { urn: urn } })
            .pipe(map((notification) => convert(NotificationRecord, notification)));
    }

    public find(req?: any): Observable<ResponseWrapper<NotificationRecord[]>> {
        const options = createRequestOption(req);
        return this.http
            .get<NotificationRecord[]>(this.resourceUrl, { ...options, observe: 'response' })
            .pipe(map((res) => convert(NotificationRecord, res)));
    }
}
