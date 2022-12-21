import { Component } from '@angular/core';
import { User } from '@app/core/model';
import { AccountUserService } from '@app/core/service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'app-delete-confirm-dialog',
    templateUrl: './delete-confirm-dialog.component.html',
})
export class DeleteConfirmDialogComponent {
    constructor(private activeModal: NgbActiveModal, private userService: AccountUserService) {}

    public user: User;

    public confirm() {
        this.userService.delete(this.user.id).subscribe(() => this.activeModal.close(true));
    }

    public clear() {
        this.activeModal.dismiss(false);
    }
}
