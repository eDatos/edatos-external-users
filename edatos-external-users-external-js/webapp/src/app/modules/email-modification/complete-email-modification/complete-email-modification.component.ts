import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AccountUserService } from '@app/core/service';
import { MessageService } from "primeng";
import { TranslateService } from "@ngx-translate/core";

@Component({
    template: ''
})
export class CompleteEmailModificationComponent implements OnInit {

    constructor(private accountUserService: AccountUserService,
                private route: ActivatedRoute,
                private messageService: MessageService,
                private translateService: TranslateService,
                private router: Router) {
    }

    ngOnInit() {
        this.route.queryParams.subscribe((params) => {
            if (params['email-update-key']) {
                this.accountUserService.confirmEmailUpdate(params['email-update-key']).subscribe(() => {
                    this.messageService.add({
                        key: 'customAlertKey',
                        severity: 'success',
                        summary: this.translateService.instant('userManagement.email-modification.messages.onConfirmationSuccessSummary'),
                        detail: this.translateService.instant('userManagement.email-modification.messages.onConfirmationSuccess'),
                        life: 5000
                    });
                    this.router.navigate(['login']);
                }, () => {
                    this.router.navigate(['login']);
                });
            } else {
                this.router.navigate(['login']);
            }
        });
    }
}
