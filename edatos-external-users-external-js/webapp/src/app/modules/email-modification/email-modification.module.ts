import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CompleteEmailModificationComponent } from "@app/modules/email-modification/complete-email-modification/complete-email-modification.component";
import { EmailModificationConfirmDialogComponent } from "@app/modules/email-modification/email-modification-confirm-dialog/email-modification-confirm-dialog.component";
import { SharedModule } from "@app/shared";
import { EmailModificationRoutingModule } from "@app/modules/email-modification/email-modification-routing.module";

@NgModule({
    declarations: [CompleteEmailModificationComponent, EmailModificationConfirmDialogComponent],
    entryComponents: [EmailModificationConfirmDialogComponent],
    imports: [CommonModule, SharedModule, EmailModificationRoutingModule],
    exports: [EmailModificationConfirmDialogComponent]
})
export class EmailModificationModule { }
