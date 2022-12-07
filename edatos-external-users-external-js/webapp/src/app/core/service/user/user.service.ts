import { ElementRef, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Credentials, User, UserAccountChangePassword } from '@app/core/model';
import { map } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { createRequestOption, ResponseUtils } from 'arte-ng/utils';
import { convert, ResponseWrapper } from '@app/core/utils/response-utils';
import { CaptchaService } from '@app/shared/service/captcha/captcha.service';

@Injectable()
export class AccountUserService {
    private resourceUrl = 'api/account';

    constructor(private http: HttpClient, private captchaService: CaptchaService) {}

    create(el: (ElementRef | string), user: User): Promise<User> {
        console.log(user)
        return this.captchaService.postRequestWithCaptcha(el, User, `${this.resourceUrl}/signup`, user, "signup");
    }

    confirmRegistration(token: string): Observable<User> {
        return this.http.get<User>(`${this.resourceUrl}/signup/confirm?token=` + token).pipe(map((res) => convert(User, res)));
    }

    login(el: (ElementRef | string), credentials: Credentials): Promise<Credentials> {
        return this.captchaService.postRequestWithCaptcha(el, User, `api/login`, credentials, "login");
    }

    buscarUsuarioPorEmail(email: string): Observable<User> {
        return this.http.get<User>(`${this.resourceUrl}/${email}/findByEmail`).pipe(map((res) => convert(User, res)));
    }

    getLogueado(): Observable<User> {
        return this.http.get<User>(`${this.resourceUrl}`).pipe(map((res) => convert(User, res)));
    }

    update(user: User): Observable<User> {
        return this.http.put<User>(this.resourceUrl, user).pipe(map((res) => convert(User, res)));
    }

    changeCurrentUserPassword(accountChangePassword: UserAccountChangePassword): Observable<User> {
        return this.http.post(`${this.resourceUrl}/change-password`, accountChangePassword).pipe(map((res) => ResponseUtils.convert(res, User)));
    }

    delete(id: number): Observable<User> {
        return this.http.delete<User>(`${this.resourceUrl}/${id}`).pipe(map((res) => convert(User, res)));
    }

    find(req?: any): Observable<ResponseWrapper<User>> {
        const options = createRequestOption(req);
        return this.http
            .get<User>(this.resourceUrl, { ...options, observe: 'response' })
            .pipe(map((res) => convert(User, res)));
    }

    get(id: number): Observable<User> {
        return this.http.get<User>(`${this.resourceUrl}/${id}`).pipe(map((res) => convert(User, res)));
    }
}
