import { ElementRef, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Credentials, User, UserAccountChangePassword } from '@app/core/model';
import { map } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { createRequestOption } from 'arte-ng/utils';
import { convert, ResponseWrapper } from '@app/core/utils/response-utils';
import { CaptchaService } from '@app/shared/service/captcha/captcha.service';
import * as FileSaver from 'file-saver';

@Injectable()
export class AccountUserService {
    private resourceUrl = 'api/account';

    constructor(private http: HttpClient, private captchaService: CaptchaService) {
    }

    create(el: ElementRef | string, user: User, userRegistrationToken?: string): Promise<User> {
        let url = `${this.resourceUrl}/signup`;
        if (userRegistrationToken) {
            url += `?token=${userRegistrationToken}`;
        }
        return this.captchaService.postRequestWithCaptcha(el, User, url, user, 'signup');
    }

    changePassword(el: ElementRef | string, accountChangePassword: UserAccountChangePassword): Promise<User> {
        console.log('changePassword', accountChangePassword);
        return this.captchaService.postRequestWithCaptcha(el, User, `${this.resourceUrl}/change-password`, accountChangePassword, 'change-password');
    }

    confirmRegistration(token: string): Observable<User> {
        return this.http.get<User>(`${this.resourceUrl}/signup/confirm?token=` + token).pipe(map((res) => convert(User, res)));
    }

    login(el: ElementRef | string, credentials: Credentials): Promise<Credentials> {
        return this.captchaService.postRequestWithCaptcha(el, User, `api/login`, credentials, 'login');
    }

    getLogueado(): Observable<User> {
        return this.http.get<User>(`${this.resourceUrl}`).pipe(map((res) => convert(User, res)));
    }

    getByRegistrationToken(token: string): Observable<User> {
        return this.http.get<User>(`${this.resourceUrl}/migrated-user-info?token=${token}`).pipe(map((res) => convert(User, res)));
    }

    update(user: User): Observable<User> {
        return this.http.put<User>(this.resourceUrl, user).pipe(map((res) => convert(User, res)));
    }

    delete(id: number): Observable<User> {
        return this.http.delete<User>(`${this.resourceUrl}/${id}`).pipe(map((res) => convert(User, res)));
    }

    find(req?: any): Observable<ResponseWrapper<User>> {
        const options = createRequestOption(req);
        return this.http.get<User>(this.resourceUrl, {
            ...options,
            observe: 'response'
        }).pipe(map((res) => convert(User, res)));
    }

    get(id: number): Observable<User> {
        return this.http.get<User>(`${this.resourceUrl}/${id}`).pipe(map((res) => convert(User, res)));
    }

    download(id: number, opts?: any): Observable<any> {
        const options = Object.assign({}, opts, { observe: 'response', responseType: 'blob' });
        return this.http.get(`${this.resourceUrl}/${id}/document/my-information`, options);
    }

    saveToFileSystem(response) {
        const contentDispositionHeader: string = response.headers.get('Content-Disposition');
        const blob = new Blob([response.body], { type: response.headers.get('content-type') + ';base64,' });
        const filename = contentDispositionHeader.match(/filename[^;=\n]*=((['"])(.*?)\2)/)[3] || 'fichero';
        FileSaver.saveAs(blob, filename);
    }
}
