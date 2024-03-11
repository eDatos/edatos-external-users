import { Component } from '@angular/core';
import { ExternalOperation } from "@app/shared/model";
import { CategoryService, ExternalOperationService } from "@app/shared/service";
import { CategoryTreeNode } from "@app/shared/components/structural-resources-tree/category-tree-node.model";
import { StructuralResourcesTreeComponent } from "@app/shared/components/structural-resources-tree/structural-resources-tree.component";

@Component({
  selector: 'app-structural-resources-tree-view',
  templateUrl: './structural-resources-tree-view.component.html',
  styleUrls: ['./structural-resources-tree-view.component.css']
})
export class StructuralResourcesTreeViewComponent extends StructuralResourcesTreeComponent {

    constructor(categoryService: CategoryService, private externalOperationService: ExternalOperationService) {
        super(categoryService, true);
    }

    public updateExternalOperationNotifications(node: CategoryTreeNode) {
        if (node.data instanceof ExternalOperation) {
            this.externalOperationService.update(node.data).subscribe((externalOperation) => {
                node.data = externalOperation;
            });
        }
    }
}
