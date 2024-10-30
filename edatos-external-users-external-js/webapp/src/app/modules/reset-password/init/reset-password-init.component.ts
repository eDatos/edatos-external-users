import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ERROR_ALERT_KEY } from '@app/app.constants';
import { ResetPasswordService } from '@app/core/service';
import { TranslateService } from '@ngx-translate/core';
import { MessageService } from 'primeng/api';

@Component({
    selector: 'app-reset-password-init',
    templateUrl: './reset-password-init.component.html'
})
export class ResetPasswordInitComponent implements OnInit {
    public email: string;

    public successSending: boolean = false;

    constructor(private resetPasswordService: ResetPasswordService, private router: Router, private messageService: MessageService, private translateService: TranslateService) {
        this.email = '';
    }

    ngOnInit() {
    }

    public requestReset() {
        this.resetPasswordService.resetPassword(this.email).subscribe((result) => {
            this.onSaveSuccess(result);
        });
    }

    private onSaveSuccess(result) {
        this.messageService.add({
            key: ERROR_ALERT_KEY,
            severity: 'success',
            detail: this.translateService.instant('global.messages.recoverPasswordEmail'),
            life: 5000
        });
        this.successSending = true;
    }

    public navigateToLogin() {
        this.router.navigate(['login']);
    }
}
