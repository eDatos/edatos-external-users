import { CommonModule, DatePipe } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CustomCalendarComponent } from '@app/shared/components/calendar/custom-calendar.component';
import { StructuralResourcesTreeModule } from '@app/shared/components/structural-resources-tree';
import { NgbModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { TranslateModule } from '@ngx-translate/core';

import { ArteAutocompleteModule, ArteEntityListEmptyModule, ArteInputModule, ArteOrderListModule, ArteSpinnerModule, ArteTableModule } from 'arte-ng';
import { FormcontrolStatusService } from 'arte-ng/directives';
import { GenericModalService } from 'arte-ng/services';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { NgArrayPipesModule } from 'ngx-pipes';
import { QuillModule } from 'ngx-quill';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { ButtonModule } from 'primeng/button';
import { CalendarModule } from 'primeng/calendar';
import { CheckboxModule } from 'primeng/checkbox';
import { ChipsModule } from 'primeng/chips';
import { DropdownModule } from 'primeng/dropdown';
import { EditorModule } from 'primeng/editor';
import { FileUploadModule } from 'primeng/fileupload';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { ListboxModule } from 'primeng/listbox';
import { OrderListModule } from 'primeng/orderlist';
import { SelectButtonModule } from 'primeng/selectbutton';
import { TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { AppAuditInfoComponent } from './components/audit-info/audit-info.component';
import { MultiLanguageEditorComponent } from './components/multi-language-editor/multi-language-editor.component';

@NgModule({
    imports: [
        CommonModule,
        RouterModule,
        ReactiveFormsModule,
        NgbModule,
        InfiniteScrollModule,
        CalendarModule,
        AutoCompleteModule,
        ChipsModule,
        ButtonModule,
        SelectButtonModule,
        ListboxModule,
        OrderListModule,
        CheckboxModule,
        InputTextareaModule,
        FileUploadModule,
        TranslateModule,
        TableModule,
        NgArrayPipesModule,
        FormsModule,
        TooltipModule,
        ArteInputModule,
        ArteTableModule,
        ArteOrderListModule,
        ArteAutocompleteModule,
        ArteSpinnerModule,
        ArteEntityListEmptyModule,
        DropdownModule,
        EditorModule,
        QuillModule,
    ],
    providers: [
        FormcontrolStatusService,
        DatePipe,
        {
            // INFRASTR-205 Way to avoid null injection of NgbModal in GenericModalService constructor
            provide: GenericModalService,
            useClass: GenericModalService,
            deps: [NgbModal],
        }, // Add at this point toa void No component factory found for <Component>. Did you add it to @NgModule.entryComponents?
    ],
    exports: [
        FormsModule,
        CommonModule,
        NgbModule,
        InfiniteScrollModule,
        AutoCompleteModule,
        ChipsModule,
        CalendarModule,
        ButtonModule,
        SelectButtonModule,
        ListboxModule,
        OrderListModule,
        CheckboxModule,
        InputTextareaModule,
        FileUploadModule,
        TranslateModule,
        TableModule,
        TooltipModule,
        NgArrayPipesModule,
        ReactiveFormsModule,
        ArteInputModule,
        ArteTableModule,
        ArteOrderListModule,
        ArteEntityListEmptyModule,
        StructuralResourcesTreeModule,
        AppAuditInfoComponent,
        MultiLanguageEditorComponent,
        CustomCalendarComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    declarations: [AppAuditInfoComponent, MultiLanguageEditorComponent, CustomCalendarComponent],
})
export class SharedModule {}
