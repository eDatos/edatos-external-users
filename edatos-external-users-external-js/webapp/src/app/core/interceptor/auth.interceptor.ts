import { Observable } from 'rxjs';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { instanceToPlain } from 'class-transformer';
import { TOKEN_AUTH_NAME } from '@app/app.constants';
import { LocalStorageService, SessionStorageService } from 'ngx-webstorage';
import { CookieService } from 'ngx-cookie-service';
import { map } from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class AuthInterceptor implements HttpInterceptor {
    constructor(private localStorage: LocalStorageService, private sessionStorage: SessionStorageService, private cookieService: CookieService) {}

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        let headers: HttpHeaders = req.headers;

        const token = this.localStorage.retrieve(TOKEN_AUTH_NAME) || this.sessionStorage.retrieve(TOKEN_AUTH_NAME);

        if (headers.get('X-Public-Request') == "1") {
            // Bypassing authentication for those requests that are marked as public because of CORS problems
            headers = headers.delete('X-Public-Request');
            return next.handle(req.clone({ headers: headers, body: instanceToPlain(req.body) }));
        }

        if (!!token) {
            headers = headers.append('Authorization', 'Bearer ' + token);
        } else {
            const tokenFromCookie = this.cookieService.get(TOKEN_AUTH_NAME);
            if (!!tokenFromCookie) {
                this.storeAuthenticationToken(tokenFromCookie, false);
                headers = headers.append('Authorization', 'Bearer ' + tokenFromCookie);
            }
        }
        return next.handle(req.clone({headers})).pipe(
            map((event: HttpEvent<any>) => {
                if (event instanceof HttpResponse) {
                    const jwt = event.headers.get(TOKEN_AUTH_NAME);
                    if (!!jwt) {
                        this.storeAuthenticationToken(jwt, false);
                    }
                }
                return event;
            })
        );
    }

    private storeAuthenticationToken(jwt, rememberMe) {
        if (rememberMe) {
            this.localStorage.store(TOKEN_AUTH_NAME, jwt);
        } else {
            this.sessionStorage.store(TOKEN_AUTH_NAME, jwt);
        }
    }
}

