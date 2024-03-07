import {
    ChangeDetectorRef,
    Component,
    DoCheck,
    EventEmitter,
    Input,
    IterableDiffer,
    IterableDiffers,
    OnChanges,
    OnInit,
    Output,
    SimpleChanges
} from '@angular/core';
import { Category, ExternalOperation, Favorite } from '@app/shared/model';
import { CategoryService } from '@app/shared/service';
import { TranslateService } from '@ngx-translate/core';
import { TreeNode } from 'primeng/api';
import { Observable, of } from 'rxjs';

/**
 * @see StructuralResourcesTreeComponent#mode
 */
export type Mode = 'view' | 'select';
export type FavoriteResource = Category | ExternalOperation;

export interface CategoryTreeNode extends TreeNode {
    data: FavoriteResource;
    parent?: CategoryTreeNode;
    children?: CategoryTreeNode[];
    editMode: 'remap' | 'name' | null;
    makingRequest?: boolean;

    setLoading(setLoadingChildren?: boolean);

    unsetLoading(setLoadingChildren?: boolean);
}

@Component({
    selector: 'app-structural-resources-tree',
    templateUrl: './structural-resources-tree.component.html',
    styleUrls: ['./structural-resources-tree.component.scss']
})
export class StructuralResourcesTreeComponent implements OnInit, DoCheck, OnChanges {
    /**
     * The list of favorites. The selection of the tree is automatically updated when favorites
     * are added or removed. Only works in 'select' {@link mode}.
     */
    @Input()
    public favorites: Favorite[] = [];

    /**
     * List of category nodes, which has been selected in a previous instance. Used to preselect categories on the tree
     */
    @Input()
    public preselectedResources?: FavoriteResource[];

    /**
     * If true, the elements on the tree become unselectable. On 'view' mode is true by default.
     *
     * @see StructuralResourcesTreeComponent#mode
     */
    @Input()
    public disabled = false;

    /**
     * Mode of the component.
     *
     * If set to 'select', the tree show checkboxes next to each element.
     * {@link StructuralResourcesTreeComponent#favorites} becomes necessary since it's where
     * the selected favorites are synchronized. This is the default mode.
     *
     * If set to 'view', the tree shows the structural resources without any checkbox and
     * they are not selectable.
     */
    @Input()
    public mode: Mode = 'select';

    /**
     * Emits an event when an element of the tree is selected. Only works in 'select' {@link mode}.
     */
    @Output()
    public onResourceSelect = new EventEmitter<CategoryTreeNode>();

    /**
     * Emits an event when an element of the tree is unselected. Only works in 'select' {@link mode}.
     */
    @Output()
    public onResourceUnselect = new EventEmitter<CategoryTreeNode>();

    public tree: CategoryTreeNode[];
    public selectedResources: CategoryTreeNode[] = [];
    public selectionMode: 'checkbox' | 'single' | 'multiple' = 'checkbox';
    public loading = false;

    private iterableDiffer: IterableDiffer<Favorite>;

    constructor(
        private categoryService: CategoryService,
        private translateService: TranslateService,
        private iterableDiffers: IterableDiffers,
        private _changeDetectorRef: ChangeDetectorRef
    ) {
        this.translateService.onLangChange.subscribe(() => {
            this._changeDetectorRef.detectChanges();
        });
        this.iterableDiffer = iterableDiffers.find([]).create();
    }

    public ngOnChanges(changes: SimpleChanges) {
        if (this.tree) {
            this.updateSelectable();
        }
    }

    public ngDoCheck(): void {
        // On the use of iterableDiffer, see https://stackoverflow.com/a/42962723/7611990.
        // Angular data-binding checking system does not check if the array has new elements, or
        // if it deleted them. When the user selects and unselect favorites, the array changes and
        // we must detect those changes to update the element selection on the tree.
        const changes = this.iterableDiffer.diff(this.favorites);
        if (changes) {
            this.updateSelection();
        }
    }

    public ngOnInit(): void {
        this.setMode();
        this.categoryService.getTree().subscribe((tree) => {
            this.createTree(tree);
        });
    }

    public createTree(tree: Category[]) {
        this.categoryListToCategoryTree(tree).subscribe((treeNodes) => {
            this.tree = treeNodes;
            this.updateSelection();
            this.updateSelectable();
        });
    }

    private updateSelectable() {
        this.tree?.forEach(node => this.updateSelectableOfNodeAndChildren(node));
    }

    private updateSelectableOfNodeAndChildren(node: CategoryTreeNode, ancestorIsSelected: boolean = false) {
        node.selectable = !this.disabled && !ancestorIsSelected;
        node.children?.forEach(childNode => {
            this.updateSelectableOfNodeAndChildren(childNode, ancestorIsSelected || this.selectedResources.includes(node));
        });
    }

    public updateSelection() {
        this.selectedResources = [];
        this.tree?.forEach(node => this.updateSelectionOfNodeAndChildren(node));
    }

    private updateSelectionOfNodeAndChildren(node: CategoryTreeNode, ancestorIsFavorite: boolean = false) {
        this.unsetLoadingNode(node);
        var nodeOrAncestorIsFavorite = ancestorIsFavorite || this.isFavorite(node.data);
        if (nodeOrAncestorIsFavorite) {
            this.selectedResources.push(node);
        } else {
            this.selectedResources = this.selectedResources.filter(selectedResource => selectedResource.data.id !== node.data.id || selectedResource.data.favoriteType !== node.data.favoriteType);
        }
        node.children?.forEach(childNode => {
            this.updateSelectionOfNodeAndChildren(childNode, nodeOrAncestorIsFavorite);
        });
    }

    public onSelect(treeNode: CategoryTreeNode) {
        if (this.onResourceSelect.observers.length > 0) {
            this.onResourceSelect.emit(treeNode);
        }
        this.updateSelectableOfNodeAndChildren(treeNode);
    }

    public onUnselect(treeNode: CategoryTreeNode) {
        if (this.onResourceUnselect.observers.length) {
            this.onResourceUnselect.emit(treeNode);
        }
        this.updateSelectableOfNodeAndChildren(treeNode);
    }

    private categoryListToCategoryTree(categories: Category[]): Observable<CategoryTreeNode[]> {
        return of(
            categories
                ?.map((category) => {
                    const children: CategoryTreeNode[] = [];
                    this.categoryListToCategoryTree(category.children).subscribe((treeNodes) => {
                        children.push(...treeNodes);
                    });
                    for (const externalOperation of category.externalOperations) {
                        children.push(this.externalOperationToTreeNode(externalOperation));
                    }
                    return this.categoryToCategoryTreeNode(category, children);
                })
        );
    }

    private sortChildren(a, b): -1 | 1 {
        if (a.data instanceof Category && b.data instanceof Category) {
            return a.data.index < b.data.index ? -1 : 1;
        } else if (a.data instanceof ExternalOperation && b.data instanceof Category) {
            return 1;
        } else if (a.data instanceof Category && b.data instanceof ExternalOperation) {
            return -1;
        } else {
            return a.data.id < b.data.id ? -1 : 1;
        }
    }

    private categoryToCategoryTreeNode(category: Category, children: CategoryTreeNode[]): CategoryTreeNode {
        const node: CategoryTreeNode = {
            label: category.name.val,
            collapsedIcon: 'fa-thin fa-folder-plus',
            expandedIcon: 'fa-solid fa-folder-minus',
            expanded: true,
            children: children.sort(this.sortChildren),
            data: category,
            selectable: !this.disabled,
            editMode: null,
            makingRequest: false,
            setLoading: (setLoadingChildren?: boolean) => this.setLoadingNode(node, setLoadingChildren),
            unsetLoading: (setLoadingChildren?: boolean) => this.unsetLoadingNode(node, setLoadingChildren),
            // We need to add key in the model to provide selection when filter
            // https://github.com/primefaces/primeng/issues/8429#issuecomment-573529483
            key: category.id
        };

        return node;
    }

    private isFavorite(resource: Category | ExternalOperation): boolean {
        return this.favorites?.some((favorite) => favorite.resource?.id === resource.id && favorite.resource?.favoriteType === resource.favoriteType) ?? false;
    }

    private setLoadingNode(node: CategoryTreeNode, setLoadingChildren = true) {
        node.icon = 'fa-thin fa-spinner fa-spin';
        node.selectable = false;
        if (node.children && setLoadingChildren) {
            for (const child of node.children) {
                this.setLoadingNode(child);
            }
        }
    }

    private unsetLoadingNode(node: CategoryTreeNode, setLoadingChildren = true) {
        if (node.data.favoriteType === 'externalOperation') {
            node.icon = 'fa-thin fa-cubes';
        } else {
            node.icon = null;
        }
        if (node.children && setLoadingChildren) {
            for (const child of node.children) {
                this.unsetLoadingNode(child);
            }
        }
    }

    private setMode() {
        switch (this.mode) {
            case 'view':
                this.selectionMode = 'single';
                this.disabled = true;
                break;
            case 'select':
                this.selectionMode = 'checkbox';
                break;
        }
    }

    private externalOperationToTreeNode(externalOperation: ExternalOperation): CategoryTreeNode {
        const node: CategoryTreeNode = {
            label: externalOperation.name.val,
            icon: 'fa-thin fa-cubes',
            data: externalOperation,
            selectable: !this.disabled,
            editMode: null,
            makingRequest: false,
            setLoading: (setLoadingChildren?: boolean) => this.setLoadingNode(node, setLoadingChildren),
            unsetLoading: (setLoadingChildren?: boolean) => this.unsetLoadingNode(node, setLoadingChildren),
            key: externalOperation.code
        };

        return node;
    }
}
