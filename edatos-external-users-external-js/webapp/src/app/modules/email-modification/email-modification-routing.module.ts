import { RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";
import { CompleteEmailModificationComponent } from "@app/modules/email-modification/complete-email-modification/complete-email-modification.component";

export const emailModificationComponent: Routes = [
    {
        path: 'confirm',
        component: CompleteEmailModificationComponent
    }
];

@NgModule({
    imports: [RouterModule.forChild(emailModificationComponent)],
    exports: [RouterModule]
})
export class EmailModificationRoutingModule {
}
