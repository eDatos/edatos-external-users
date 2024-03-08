import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MultiLanguageInputModule } from '@app/shared/components/multi-language-input/multi-language-input.module';
import { TranslateModule } from '@ngx-translate/core';
import { ArteDirectivesModule } from 'arte-ng/directives';
import { TreeDragDropService } from 'primeng/api';
import { DialogModule } from 'primeng/dialog';
import { InputSwitchModule } from 'primeng/inputswitch';
import { InputTextModule } from 'primeng/inputtext';
import { TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { TreeModule } from 'primeng/tree';
import { ExternalCategoriesTableComponent } from './external-categories-table/external-categories-table.component';
import { StructuralResourcesTreeViewComponent } from "@app/shared/components/structural-resources-tree/structural-resources-tree-view/structural-resources-tree-view.component";
import { StructuralResourcesTreeSelectComponent } from "@app/shared/components/structural-resources-tree/structural-resources-tree-select/structural-resources-tree-select.component";
import { StructuralResourcesTreeSimpleSelectComponent } from "@app/shared/components/structural-resources-tree/structural-resources-tree-simple-select/structural-resources-tree-simple-select.component";
import { StructuralResourcesTreeEditComponent } from "@app/shared/components/structural-resources-tree/structural-resources-tree-edit/structural-resources-tree-edit.component";

@NgModule({
    declarations: [
        ExternalCategoriesTableComponent,
        StructuralResourcesTreeViewComponent,
        StructuralResourcesTreeSelectComponent,
        StructuralResourcesTreeSimpleSelectComponent,
        StructuralResourcesTreeEditComponent
    ],
    imports: [
        CommonModule,
        TreeModule,
        TranslateModule,
        FormsModule,
        InputTextModule,
        ArteDirectivesModule,
        DialogModule,
        MultiLanguageInputModule,
        TableModule,
        TooltipModule,
        InputSwitchModule,
    ],
    exports: [
        StructuralResourcesTreeViewComponent,
        StructuralResourcesTreeSimpleSelectComponent,
        StructuralResourcesTreeSelectComponent,
        StructuralResourcesTreeEditComponent
    ],
    providers: [TreeDragDropService],
})
export class StructuralResourcesTreeModule {}
