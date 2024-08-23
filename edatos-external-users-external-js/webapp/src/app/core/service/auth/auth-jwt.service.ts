import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LocalStorageService, SessionStorageService } from 'ngx-webstorage';
import { CookieService } from 'ngx-cookie-service';
import { TOKEN_AUTH_NAME } from '@app/app.constants';
import { ConfigService } from '@app/config';

@Injectable()
export class AuthServerProvider {
    constructor(
        private $localStorage: LocalStorageService,
        private $sessionStorage: SessionStorageService,
        private cookieService: CookieService,
        private configService: ConfigService
    ) {}

    getToken() {
        const token = this.$localStorage.retrieve(TOKEN_AUTH_NAME) || this.$sessionStorage.retrieve(TOKEN_AUTH_NAME);
        if (!token) {
            return this.cookieService.get(TOKEN_AUTH_NAME);
        }
        return token;
    }

    loginWithToken(jwt, rememberMe) {
        if (jwt) {
            this.storeAuthenticationToken(jwt, rememberMe);
            return Promise.resolve(jwt);
        } else {
            return Promise.reject('auth-jwt-service Promise reject'); // Put appropriate error message here
        }
    }

    storeAuthenticationToken(jwt, rememberMe) {
        if (rememberMe) {
            this.$localStorage.store(TOKEN_AUTH_NAME, jwt);
        } else {
            this.$sessionStorage.store(TOKEN_AUTH_NAME, jwt);
        }
    }

    logout(): Observable<any> {
        return new Observable((observer) => {
            Edatos.UserManagement.refreshAuthenticationState();
            const config = this.configService.getConfig();
            this.$localStorage.clear(TOKEN_AUTH_NAME);
            this.$sessionStorage.clear(TOKEN_AUTH_NAME);
            this.cookieService.delete(TOKEN_AUTH_NAME, this.getLocation(config.endpoint.appUrl).pathname);
            observer.complete();
        });
    }

    private getLocation(href) {
        const l = document.createElement('a');
        l.href = href;
        return l;
    }
}
