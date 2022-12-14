import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { SharedModule } from '@app/shared';
import { SignupRoutingModule, SignupFormComponent } from '.';
import {ArteCalendarModule} from "arte-ng";

@NgModule({
    imports: [SharedModule, SignupRoutingModule, ArteCalendarModule],
    declarations: [SignupFormComponent],
    entryComponents: [SignupFormComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class SignupModule {}
