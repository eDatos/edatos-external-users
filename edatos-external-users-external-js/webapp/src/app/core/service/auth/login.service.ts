import { Injectable } from '@angular/core';
import { AuthServerProvider } from './auth-jwt.service';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class LoginService {
    constructor(private http: HttpClient, private authServerProvider: AuthServerProvider) {}

    logout() {
        return this.http.post<any>(`api/account/logout`, undefined).toPromise().finally(() => {
            Edatos.UserManagement.refreshAuthenticationState();
            this.authServerProvider
                .logout()
                .toPromise()
                .then(() => { });
        });
    }
}
