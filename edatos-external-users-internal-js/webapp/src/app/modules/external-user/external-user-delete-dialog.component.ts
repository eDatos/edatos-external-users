import { Component } from '@angular/core';
import { ExternalUser } from '@app/core/model';
import { ExternalUserService } from '@app/core/service/user';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ArteEventManager } from 'arte-ng/services';

@Component({
    selector: 'app-external-user-delete-dialog',
    templateUrl: './external-user-delete-dialog.component.html',
})
export class ExternalUserDeleteDialogComponent {
    public static EVENT_NAME = 'UserModified';

    public user: ExternalUser;

    constructor(private externalUserService: ExternalUserService, public activeModal: NgbActiveModal, private eventManager: ArteEventManager) {}

    public clear() {
        this.activeModal.dismiss(false);
    }

    public confirmAction(id) {
        if (this.user.id === id) {
            if (this.user.deletionDate) {
                this.confirmRestore(id);
            } else {
                this.confirmDelete(id);
            }
        }
    }

    private confirmDelete(id) {
        this.externalUserService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: ExternalUserDeleteDialogComponent.EVENT_NAME,
                content: response,
            });
            this.activeModal.dismiss('deleted');
        });
    }

    private confirmRestore(id) {
        this.externalUserService.restore(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: ExternalUserDeleteDialogComponent.EVENT_NAME,
                content: response,
            });
            this.activeModal.dismiss('restored');
        });
    }
}
