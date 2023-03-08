import { LocalStorageService, SessionStorageService } from 'ngx-webstorage';
import { CookieService } from 'ngx-cookie';

import { TOKEN_AUTH_NAME } from '@app/app.constants';
import { ConfigService } from '@app/config';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { instanceToPlain } from 'class-transformer';

@Injectable({
    providedIn: 'root',
})
export class AuthInterceptor implements HttpInterceptor {
    constructor() {}

    intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
        return next.handle(request.clone({ body: instanceToPlain(request.body) }));
    }
}
