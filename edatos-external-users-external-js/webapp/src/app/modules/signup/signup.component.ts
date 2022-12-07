import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { User, Role, Language } from '@app/core/model';
import { Router } from '@angular/router';
import { AccountUserService } from '@app/core/service/user';
import { CaptchaService } from '@app/shared/service';
import { MessageService } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';
import { DataProtectionPolicyService } from "@app/modules/account/data-protection-policy/data-protection-policy.service";

type SignUp = Omit<User, 'id' | 'roles'>;

@Component({
    selector: 'app-signup',
    templateUrl: './signup.component.html',
})
export class SignupFormComponent implements OnInit {
    public user: User = new User();
    public isSaving: Boolean = false;
    public validUser = false;
    private paramLogin: string;
    public rolesEnum = Role;
    public languageEnum = Language;

    public confirmPassword: string;

    private captchaContainerEl: ElementRef;

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
        private captchaService: CaptchaService,
        private messageService: MessageService,
        private translateService: TranslateService,
        private dataProtectionPolicyService: DataProtectionPolicyService
    ) {}

    ngOnInit() {
        this.isSaving = false;
    }

    clear() {
        const returnPath = ['signup'];
        if (this.paramLogin) {
            returnPath.push(this.paramLogin);
        }
        this.router.navigate(returnPath);
    }

    save() {
        this.acceptDataProteccionPolicy()
        console.log(this.user)
        this.isSaving = true;
        if (this.passwordDoNotMatch()) {
            this.validUser = false;
        } else {
            console.log(this.user)
            this.accountUserService.create(this.captchaContainerEl, this.user).then((response) => this.onSaveSuccess(response)).finally(() => this.isSaving = false);
        }
    }

    private passwordDoNotMatch(): boolean {
        return this.user.password !== this.confirmPassword;
    }

    navigateToLogin() {
        this.router.navigate(['login']);
    }

    private onSaveSuccess(result) {
        this.messageService.add({
            key: 'customAlertKey',
            severity: 'success',
            summary: this.translateService.instant('signup.messages.onSuccessSumary'),
            detail: this.translateService.instant('signup.messages.onRequest'),
            life: 5000,
        });
        this.isSaving = false;
        this.navigateToLogin();
    }
    acceptDataProteccionPolicy() {
        this.dataProtectionPolicyService.getDataProtectionPolicy().subscribe((dataProtectionPolicy) => {
            console.log(dataProtectionPolicy.id)
            this.user.dataProtectionPolicyId = dataProtectionPolicy.id;
            console.log(this.user.dataProtectionPolicyId)
        });
    }
}
