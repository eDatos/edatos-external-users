import { HttpClient } from '@angular/common/http';
import { ElementRef, Injectable } from '@angular/core';
import { convert } from '@app/core/utils/response-utils';
import { TranslateService } from '@ngx-translate/core';
import { map } from 'rxjs/operators';


@Injectable({
    providedIn: 'root'
})
export class CaptchaService {

    private defaultOptions = {
        labelText: '',
        buttonText: ''
    };

    constructor(translateService: TranslateService, private http: HttpClient) {
        translateService.get(['captcha.label.text', 'captcha.button.text']).subscribe(translations => {
            this.defaultOptions = {
                labelText: translations['captcha.label.text'] === 'captcha.label.text' ? '' : translations['captcha.label.text'],
                buttonText: translations['captcha.button.text'] === 'captcha.button.text' ? '' : translations['captcha.button.text']
            };
        });
    }

    /*
    * el: the element (or its id) where the captcha is going to be drawn as its last child
    * resultClass: the class of the result of the request
    * relativeUrl: relative url of the request, equivalent to the first parameter of the HttpClient.post<>() method
    * body: the body of the request
    * action: a descriptive identifier of the action where the captcha is being used
    *
    * This function return a Promise that is resolved when the captcha has been solved correctly and the request has been successful.
    * If the captcha gets a wrong answer, then another one will appear without having to call this method again.
    * In other hand, that promise is rejected when the captcha has given an unexpected error or it has been solved correctly but the request was wrong.
    */
    postRequestWithCaptcha<T, G>(el: (ElementRef | string), resultClass: new () => G, relativeUrl: string, body: T, action: string): Promise<G> {
        const url = (new URL(relativeUrl, window.location.origin)).href;
        const requestMethod = (url: String): Promise<G> => {
            return this.http.post<G>(url.match(new RegExp(window.location.origin + '/?(.*$)'))[1], body).pipe(map((res) => convert(resultClass, res))).toPromise();
        };

        const options = {
            ...this.defaultOptions,
            ...((el instanceof ElementRef) ? { captchaEl: el.nativeElement } : { captchaId: el }),
            action: 'external_users_' + action
        };
        return Edatos.captcha.requestWithCaptcha(requestMethod, url, options);
    }

    buildCaptcha(el: (ElementRef | string)): void {
        return Edatos.captcha.showCaptcha((el instanceof ElementRef) ? {
            ...this.defaultOptions,
            captchaEl: el.nativeElement
        } : { ...this.defaultOptions, captchaId: el });
    }
}
