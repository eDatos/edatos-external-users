import { Component, DoCheck, Input, IterableDiffer, IterableDiffers, OnChanges, SimpleChanges } from '@angular/core';
import { Favorite, FavoriteResource } from "@app/shared/model";
import { StructuralResourcesTreeComponent } from "@app/shared/components/structural-resources-tree/structural-resources-tree.component";
import { CategoryTreeNode } from "@app/shared/components/structural-resources-tree/category-tree-node.model";
import { CategoryService } from "@app/shared/service";

@Component({
  selector: 'app-structural-resources-tree-select',
  templateUrl: './structural-resources-tree-select.component.html',
  styleUrls: ['./structural-resources-tree-select.component.css']
})
export class StructuralResourcesTreeSelectComponent extends StructuralResourcesTreeComponent implements DoCheck, OnChanges {

    /**
     * The list of favorites. The selection of the tree is automatically updated when favorites
     * are added or removed. Only works in 'select' {@link mode}.
     */
    @Input()
    public favorites: Favorite[] = [];

    /**
     * If true, the elements on the tree become unselectable.
     *
     * @see StructuralResourcesTreeComponent#mode
     */
    @Input()
    public disabled = false;

    public selectedResources: CategoryTreeNode[] = [];
    private iterableDiffer: IterableDiffer<Favorite>;

    constructor(categoryService: CategoryService, private iterableDiffers: IterableDiffers) {
        super(categoryService, true);
        this.iterableDiffer = iterableDiffers.find([]).create();
    }

    public ngDoCheck(): void {
        // On the use of iterableDiffer, see https://stackoverflow.com/a/42962723/7611990.
        // Angular data-binding checking system does not check if the array has new elements, or
        // if it deleted them. When the user selects and unselect favorites, the array changes and
        // we must detect those changes to update the element selection on the tree.
        const changes = this.iterableDiffer.diff(this.favorites);
        if (changes) {
            this.updateSelection();
            this.updateSelectable();
        }
    }

    public ngOnChanges(changes: SimpleChanges) {
        if (this.tree && changes.disabled) {
            this.updateSelectable();
        }
    }

    protected afterTreeCreation() {
        this.updateSelection();
        this.updateSelectable();
    };

    private updateSelectable() {
        this.tree?.forEach(node => this.updateSelectableOfNodeAndChildren(node));
    }

    public updateSelectableOfNodeAndChildren(node: CategoryTreeNode, ancestorIsSelected: boolean = false) {
        node.selectable = !this.disabled && !ancestorIsSelected;
        node.children?.forEach(childNode => {
            this.updateSelectableOfNodeAndChildren(childNode, ancestorIsSelected || this.selectedResources.includes(node));
        });
    }

    updateSelection() {
        this.selectedResources = [];
        this.tree?.forEach(node => this.updateSelectionOfNodeAndChildren(node, false));
    }

    private updateSelectionOfNodeAndChildren(node: CategoryTreeNode, ancestorIsFavorite: boolean = false) {
        var nodeOrAncestorIsFavorite = ancestorIsFavorite || this.isFavorite(node.data);
        if (nodeOrAncestorIsFavorite) {
            this.selectedResources.push(node);
        }
        node.children?.forEach(childNode => {
            this.updateSelectionOfNodeAndChildren(childNode, nodeOrAncestorIsFavorite);
        });
    }

    private isFavorite(resource: FavoriteResource): boolean {
        return this.favorites?.some((favorite) => favorite.resource?.id === resource.id && favorite.resource?.favoriteType === resource.favoriteType) ?? false;
    }
}
