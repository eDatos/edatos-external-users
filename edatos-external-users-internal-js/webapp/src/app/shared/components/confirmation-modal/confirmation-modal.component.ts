import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'app-confirmation-modal',
    templateUrl: './confirmation-modal.component.html',
})
export class ConfirmationModal {
    constructor(private activeModal: NgbActiveModal) {}

    public headerText: string;
    public headerParams: any = {};
    public bodyText: string;
    public bodyParams: any = {};
    public isDangerous: boolean = false;

    public confirm() {
        this.activeModal.close(true);
    }

    public clear() {
        this.activeModal.dismiss(false);
    }
}
