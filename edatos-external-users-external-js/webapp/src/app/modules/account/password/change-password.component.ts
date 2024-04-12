import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { AccountUserService, Principal } from '@app/core/service';
import { User, UserAccountChangePassword } from '@app/core/model';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';
import { CaptchaService } from '@app/shared/service';

@Component({
    selector: 'change-password',
    templateUrl: './change-password.component.html'
})
export class ChangePasswordComponent implements OnInit {
    public isSaving: boolean = false;

    public account: any;
    public currentPassword: string;
    public newPassword: string;
    public confirmNewPassword: string;

    private captchaContainerEl: ElementRef;

    @ViewChild('captchaContainer')
    set captchaContainer(captchaContainerEl: ElementRef) {
        if (captchaContainerEl && !this.captchaContainerEl) {
            this.captchaContainerEl = captchaContainerEl;
            this.captchaService.buildCaptcha(this.captchaContainerEl);
        }
    }

    constructor(
        private userService: AccountUserService,
        private principal: Principal,
        private router: Router,
        private messageService: MessageService,
        private translateService: TranslateService,
        private captchaService: CaptchaService
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.principal.identity().then((account) => {
            this.account = account;
        });
    }

    clear(): void {
        const returnPath = ['account-management'];
        this.router.navigate(returnPath);
    }

    save() {

        if (this.newPassword !== this.confirmNewPassword) {
            return;
        }
        this.isSaving = true;
        const accountChangePassword: UserAccountChangePassword = {
            currentPassword: this.currentPassword,
            newPassword: this.newPassword
        };
        this.userService.changePassword(this.captchaContainerEl, accountChangePassword)
            .then((res) => this.onSaveSuccess(res),
                (error) => this.onSaveError(error)
            )
            .finally(() => (this.isSaving = false));
    }

    private onSaveSuccess(result: User): void {
        this.messageService.add({
            key: 'customAlertKey',
            severity: 'success',
            summary: this.translateService.instant('userManagement.password.messages.onSuccessSumary'),
            detail: this.translateService.instant('userManagement.password.messages.onSuccess'),
            life: 5000
        });
        this.clear();
    }

    private onSaveError(error: any): void {
        this.isSaving = false;
    }
}
