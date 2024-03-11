import { Component, Input } from '@angular/core';
import { FavoriteResource } from "@app/shared/model";
import { StructuralResourcesTreeComponent } from "@app/shared/components/structural-resources-tree/structural-resources-tree.component";
import { CategoryTreeNode } from "@app/shared/components/structural-resources-tree/category-tree-node.model";
import { CategoryService } from "@app/shared/service";

@Component({
  selector: 'app-structural-resources-tree-simple-select',
  templateUrl: './structural-resources-tree-simple-select.component.html',
  styleUrls: ['./structural-resources-tree-simple-select.component.css']
})
export class StructuralResourcesTreeSimpleSelectComponent extends StructuralResourcesTreeComponent {

    /**
     * List of category nodes, which has been selected in a previous instance. Used to preselect categories on the tree
     */
    @Input()
    public preselectedResources?: FavoriteResource[];

    public selectedResources: CategoryTreeNode[] = [];

    constructor(categoryService: CategoryService) {
        super(categoryService, true);
    }

    protected afterTreeCreation() {
        this.updateSelection();
    };

    protected updateSelection() {
        this.selectedResources = [];
        this.tree?.forEach(node => this.updateSelectionOfNodeAndChildren(node));
    }

    private updateSelectionOfNodeAndChildren(node: CategoryTreeNode) {
        if (this.isPreselected(node.data)) {
            this.selectedResources.push(node);
        }
        node.children?.forEach(childNode => {
            this.updateSelectionOfNodeAndChildren(childNode);
        });
    }

    private isPreselected(resource: FavoriteResource): boolean {
        return this.preselectedResources?.some((r) => r.id === resource.id && r.favoriteType === resource.favoriteType) ?? false;
    }
}
