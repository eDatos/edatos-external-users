import { Component, ElementRef, Inject, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Credentials } from '@app/core/model';
import { AccountUserService, ExternalLoginService } from '@app/core/service/user';
import { AuthServerProvider, Principal } from '@app/core/service';
import { DOCUMENT } from '@angular/common';
import { addQueryParamToRoute } from '@app/shared/utils/routesUtils';
import { CaptchaService } from '@app/shared/service';
import { MessageService } from 'primeng';
import { TranslateService } from '@ngx-translate/core';


@Component({
    selector: 'app-login',
    templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit {
    credentials: Credentials;
    origin: String;
    captchaContainerEl: ElementRef;
    passwordVisible: boolean = false;

    @ViewChild('captchaContainer')
    set captchaContainer(captchaContainerEl: ElementRef) {
        if (captchaContainerEl && !this.captchaContainerEl) {
            this.captchaContainerEl = captchaContainerEl;
            this.captchaService.buildCaptcha(this.captchaContainerEl);
        }
    }

    constructor(
        private accountUserService: AccountUserService,
        private router: Router,
        private principal: Principal,
        private route: ActivatedRoute,
        private authServerProvider: AuthServerProvider,
        @Inject(DOCUMENT) readonly document: Document,
        private captchaService: CaptchaService,
        private externalUserService: ExternalLoginService,
        private messageService: MessageService,
        private translateService: TranslateService
    ) {
        this.credentials = new Credentials();
        this.route.queryParams.subscribe((queryParams) => {
            if (queryParams['origin']) {
                this.origin = queryParams['origin'];
                this.externalUserService.urlToReturnToAfterLogin = this.origin.toString();
            } else {
                this.origin = this.externalUserService.urlToReturnToAfterLogin;
            }
        });
    }

    ngOnInit() {
        this.route.queryParams.subscribe((params) => {
            if (params['registration-key']) {
                const urlWithoutRegistrationKeyToken = new URL(document.URL);
                urlWithoutRegistrationKeyToken.searchParams.delete('registration-key');
                window.history.pushState({}, '', urlWithoutRegistrationKeyToken.href);

                this.accountUserService.confirmRegistration(params['registration-key']).subscribe(() => {
                    this.messageService.add({
                        key: 'customAlertKey',
                        severity: 'success',
                        summary: this.translateService.instant('signup.messages.onSuccessSumary'),
                        detail: this.translateService.instant('signup.messages.onSuccess'),
                        life: 5000
                    });
                });
            }
        });
    }

    login() {
        this.accountUserService.login(this.captchaContainerEl, this.credentials).then(() => {
            this.principal.identity().then(() => {
                if (this.origin && !this.isOriginFromExternalUsers()) {
                    this.navigateToOrigin();
                } else {
                    this.navigateToFilter();
                }
            });
        });
    }

    private isOriginFromExternalUsers(): boolean {
        try {
            let loginUrl = new URL(window.location.href);
            let loginBaseUrlString = (loginUrl.origin + loginUrl.pathname).replace(/\/$/, "");

            let originUrl = new URL(this.origin.toString());
            let originBaseUrlString = (originUrl.origin + originUrl.pathname).replace(/\/$/, "");
            return loginBaseUrlString === originBaseUrlString;
        } catch (error) {
            return false;
        }
    }

    private navigateToFilter() {
        this.router.navigate(['filter']);
    }

    private navigateToOrigin() {
        const originRouteWithTokenParam = addQueryParamToRoute(window.location.href.replace(/\/#.*/, '/api/authenticate'), 'origin', this.origin.toString());
        this.document.defaultView.open(originRouteWithTokenParam, '_self');
    }

    public navigateToSignup() {
        this.router.navigate(['signup']);
    }

    public navigateToDataProtectionPolicy() {
        this.router.navigate(['data-protection-policy']);
    }

    public navigateToResetPassword() {
        this.router.navigate(['reset-password']);
    }

    public togglePasswordVisibility() {
        this.passwordVisible = !this.passwordVisible;
    }
}
