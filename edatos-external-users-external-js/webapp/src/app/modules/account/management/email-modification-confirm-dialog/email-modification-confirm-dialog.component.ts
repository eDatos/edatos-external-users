import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from "@app/core/model";
import { AccountUserService } from "@app/core/service";
import { MessageService } from "primeng/api";
import { TranslateService } from "@ngx-translate/core";

@Component({
    selector: 'app-email-modification-confirm-dialog',
    templateUrl: './email-modification-confirm-dialog.component.html',
})
export class EmailModificationConfirmDialogComponent {

    private account: User;
    private newEmail: string;

    constructor(private activeModal: NgbActiveModal, private userService: AccountUserService,
                private messageService: MessageService, private translateService: TranslateService) {}

    public confirm() {
        this.account.email = this.newEmail;
        this.userService.updateEmail(this.account).subscribe(() => {
            this.messageService.add({
                key: 'customAlertKey',
                severity: 'success',
                summary: this.translateService.instant('userManagement.email-modification.messages.onRequestSuccessSummary'),
                detail: this.translateService.instant('userManagement.email-modification.messages.onRequestSuccess'),
                life: 5000,
            });
        });
        this.activeModal.close(true);
    }

    public clear() {
        this.activeModal.dismiss(false);
    }
}
