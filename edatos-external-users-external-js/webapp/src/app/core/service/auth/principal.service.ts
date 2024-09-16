import { Injectable } from '@angular/core';
import { Role, UserAccount } from '@app/core/model';
import { Observable, Subject } from 'rxjs';
import { AccountUserService } from '../user/user.service';
import { AuthServerProvider } from './auth-jwt.service';

@Injectable()
export class Principal {
    private authenticationState = new Subject<boolean>();

    constructor(private authServerProvider: AuthServerProvider, private accountUserService: AccountUserService) {}

    hasRoles(rolesRuta: Role[]): Promise<boolean> {
        return Promise.resolve(this.rolesRutaMatchesRolesUsuario(rolesRuta));
    }

    rolesRutaMatchesRolesUsuario(rolesRuta: Role[]) {
        return true;
    }

    getAuthenticationState(): Observable<boolean> {
        return this.authenticationState.asObservable();
    }

    identity(): Promise<UserAccount> {
        return new Promise((resolve, reject) => {
            const token: string = this.authServerProvider.getToken();
            if (token) {
                this.accountUserService
                    .getLogueado()
                    .toPromise()
                    .then((user) => {
                        this.authenticationState.next(true);
                        resolve(UserAccount.fromJwt(token));
                    })
                    .catch(() => {
                        this.authenticationState.next(false);
                        resolve(null);
                    })
                    .finally(() => {
                        Edatos.UserManagement.refreshAuthenticationState();
                    });
            } else {
                this.authenticationState.next(false);
                resolve(null);
                Edatos.UserManagement.refreshAuthenticationState();
            }
        });
    }

    public isCorrectlyLogged(): Observable<boolean> {
        return this.getAuthenticationState();
    }
}
