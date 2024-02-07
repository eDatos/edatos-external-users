import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Notification } from '@app/shared/model';
import { convert } from '@app/core/utils/response-utils';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class NotificationService {
    public readonly resourceUrl = 'api/notifications';

    constructor(private http: HttpClient) {}

    public send(notification: Notification): Observable<Notification> {
        return this.http.post<Notification>(this.resourceUrl, notification).pipe(map((notice) => convert(Notification, notice)));
    }

    public findReceivers(req?: any): Observable<Notification> {
        return this.http.get<Notification>(this.resourceUrl + "/receivers", { params: req }).pipe(map((res) => convert(Notification, res)));
    }
}
