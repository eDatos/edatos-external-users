import { ChangeDetectorRef, Component, DoCheck, EventEmitter, Input, IterableDiffer, IterableDiffers, OnChanges, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { MultiLanguageInputComponent } from '@app/shared/components/multi-language-input';
import { DEFAULT_LOCALE } from '@app/shared/components/multi-language-input/multi-language-input.constants';
import { Category, ExternalCategory, ExternalOperation, Favorite, FavoriteResource, InternationalString } from '@app/shared/model';
import { CategoryService, ExternalOperationService, LanguageService } from '@app/shared/service';
import { TranslateService } from '@ngx-translate/core';
import * as _ from 'lodash';
import { Tree } from 'primeng';
import { TreeNode } from 'primeng/api';
import { Observable, of } from 'rxjs';
import { finalize, shareReplay } from 'rxjs/operators';

/**
 * @see StructuralResourcesTreeComponent#mode
 */
export type Mode = 'view' | 'select' | 'simpleSelect' | 'edit';

export interface CategoryTreeNode extends TreeNode {
    data: FavoriteResource;
    parent?: CategoryTreeNode;
    children?: CategoryTreeNode[];
    editMode: 'remap' | 'name' | null;
    makingRequest?: boolean;
}

@Component({
    selector: 'app-structural-resources-tree',
    templateUrl: './structural-resources-tree.component.html',
    styleUrls: ['./structural-resources-tree.component.scss'],
})
export class StructuralResourcesTreeComponent implements OnInit, DoCheck, OnChanges {

    protected readonly DEFAULT_LOCALE = DEFAULT_LOCALE;

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
     *
     * If set to 'edit', the tree allows to create, delete, and move around nodes. This
     * mode it's meant to map the elements of the tree to categories and operations.
     */
    @Input()
    public mode: Mode = 'select';

    /**
     * Determines whether the selection of a parent node propagates down to its descendants, and
     * whether the selection of all children propagates up to its parents. Only works in 'select' {@link mode}.
     */
    @Input()
    public propagateSelection: boolean;

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

    @ViewChild(MultiLanguageInputComponent)
    public languageInputComponent: MultiLanguageInputComponent;

    @ViewChild(Tree)
    public ptree: Tree;

    public tree: CategoryTreeNode[];
    public selectedResources: CategoryTreeNode[] = [];
    public selectionMode: 'checkbox' | 'single' | 'multiple' = 'checkbox';
    public enableDragAndDrop = false;
    public allowedLanguages: string[];
    public clickedDelete: boolean;

    public isNodeSaving = false;
    public loading = false;
    private iterableDiffer: IterableDiffer<Favorite>;
    private nodeList: CategoryTreeNode[] = [];


    constructor(
        private categoryService: CategoryService,
        private translateService: TranslateService,
        private externalOperationService: ExternalOperationService,
        private iterableDiffers: IterableDiffers,
        private languageService: LanguageService,
        private _changeDetectorRef: ChangeDetectorRef
    ) {
        this.translateService.onLangChange.subscribe(() => {
            this._changeDetectorRef.detectChanges();
        });
        this.iterableDiffer = iterableDiffers.find([]).create();
        // sharedReplay RxJS operator allows for caching the http response, since
        // languages are not that frequently updated (and can be updated just by
        // reloading the page, the cache just avoids unnecessary requests every time
        // a multi-language input component is opened).
        this.languageService
            .getAllowed()
            .pipe(shareReplay({ bufferSize: 1, refCount: true }))
            .subscribe((languages) => (this.allowedLanguages = languages));
    }

    public ngOnChanges(changes: SimpleChanges) {
        if (changes.disabled) {
            for (const node of this.nodeList) {
                node.selectable = !this.disabled;
            }
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

    public isEditMode(): boolean {
        return this.mode === 'edit';
    }

    public isEditModeOrFiltered(): boolean {
        if(this.ptree && this.ptree.filteredNodes){
            return false;
        }
        return this.isEditMode();
    }

    public createTree(tree: Category[]) {
        this.nodeList = [];
        this.categoryListToCategoryTree(tree).subscribe((treeNodes) => {
            this.tree = treeNodes;
        });
    }

    public updateSelection() {
        this.selectedResources = [];
        for (const node of this.nodeList) {
            this.unsetLoadingNode(node);
            if (this.isFavorite(node.data)) {
                this.selectedResources.push(node);
            }
        }
    }

    public onSelect(treeNode: CategoryTreeNode) {
        if (this.onResourceSelect.observers.length > 0) {
            this.onResourceSelect.emit(treeNode);
        }
    }

    public onUnselect(treeNode: CategoryTreeNode) {
        if (this.onResourceUnselect.observers.length) {
            this.onResourceUnselect.emit(treeNode);
        }
    }

    public addNode(parent?: CategoryTreeNode) {
        const category = new Category();
        const node: CategoryTreeNode = {
            label: '',
            collapsedIcon: 'fa fa-folder',
            expandedIcon: 'fa fa-folder-open',
            expanded: true,
            data: category,
            children: [],
            selectable: !this.disabled,
            editMode: null,
            parent: parent
        };
        if (parent) {
            if (!parent.children) {
                parent.children = [];
            }
            parent.children.push(node);
            category.index = parent.children.length;
            if (parent.data instanceof Category) {
                parent.data.children.push(category);
            }
        } else {
            category.index = this.tree.length;
            this.tree.push(node);
        }
        this.nodeList.push(node);
        this.editNodeName(node);
    }

    public deleteNode(node: CategoryTreeNode) {
        _.pull(this.nodeList, node);
        if (node.parent) {
            _.pull(node.parent.children!, node);
            if (node.parent.data instanceof Category) {
                _.pull(node.parent.data?.children, node.data);
            }
        } else {
            _.pull(this.tree, node);
        }
        if (node.data.id) {
            this.loading = true;
            this.categoryService
                .deleteCategory(node.data.id)
                .pipe(
                    finalize(() => {
                        this.ngOnInit();
                        this.loading = false;
                    })
                )
                .subscribe();
        }
    }

    public saveNodeName(node: CategoryTreeNode, name: InternationalString) {
        this.disableEditButtons();
        if (node.data.id) {
            this.updateCategoryName(node, name);
        } else {
            this.createCategoryName(node, name);
        }
    }

    public cancelNodeEdit(node: CategoryTreeNode) {
        if (node.editMode === 'name') {
            if (node.data.name.isEmptyOrBlank()) {
                this.deleteNode(node);
            }
            this.enableEditButtons();
        }
        this.closeEditDialog(node);
    }

    public editNodeName(node: CategoryTreeNode) {
        node.editMode = 'name';
    }

    public remapNode(node: CategoryTreeNode) {
        node.editMode = 'remap';
    }

    public saveRemap(node: CategoryTreeNode, selectedResources: ExternalCategory[]) {
        if (node.data instanceof Category) {
            node.data.externalCategories = selectedResources;
        }
        this.closeEditDialog(node);
        this.updateTree();
    }

    public onNodeDrop(event: { dragNode: CategoryTreeNode; dropNode: CategoryTreeNode } & ({ index: number } | { dropIndex: number })) {
        this.loading = true;
        this.reconstructTree(this.tree);
        this.categoryService
            .updateTree(this.tree.map((node) => node.data as Category))
            .pipe(finalize(() => (this.loading = false)))
            .subscribe({
                next: (tree) => this.createTree(tree),
                error: () => this.ngOnInit(),
            });
    }

    public getDisabledExternalCategories(selectedCategory: Category): ExternalCategory[] {
        const externalCategoriesAlreadySelectedByOtherNode: ExternalCategory[] = [];
        const categoryNodes = this.nodeList.filter((node) => node.data.favoriteType === 'category');
        for (const node of categoryNodes) {
            if (node.data.id !== selectedCategory.id) {
                externalCategoriesAlreadySelectedByOtherNode.push(...(node.data as Category).externalCategories);
            }
        }
        return externalCategoriesAlreadySelectedByOtherNode;
    }

    public enableNotifications(node: CategoryTreeNode): void {
        if (node.data instanceof ExternalOperation) {
            node.data.notificationsEnabled = true;
        }
    }

    public disableNotifications(node: CategoryTreeNode): void {
        if (node.data instanceof ExternalOperation) {
            node.data.notificationsEnabled = false;
        }
    }

    public updateExternalOperationNotifications(node: CategoryTreeNode) {
        if (node.data instanceof ExternalOperation) {
            this.externalOperationService.update(node.data).subscribe((externalOperation) => {
                node.data = externalOperation;
            });
        }
    }

    private updateTree() {
        this.categoryService
            .updateTree(this.tree.map((node) => node.data as Category))
            .pipe(finalize(() => (this.loading = false)))
            .subscribe({
                next: (tree) => this.createTree(tree)
            });
    }

    private updateCategoryName(category: CategoryTreeNode, name: InternationalString) {
        this.categoryService
            .updateCategory({...category.data, parentId: category.parent?.data?.id, name} as Category)
            .subscribe({
                next: (savedCategory) => {
                    this.closeEditDialog(category);
                    this.enableEditButtons();
                    this.updateCategoryTreeNode(category, savedCategory);
                },
                error: () => this.enableEditButtons()
            });
    }

    private createCategoryName(category: CategoryTreeNode, name: InternationalString) {
        this.categoryService
            .createCategory({...category.data, parentId: category.parent?.data?.id, name} as Category)
            .subscribe({
                next: (savedCategory) => {
                    this.closeEditDialog(category);
                    this.enableEditButtons();
                    this.updateCategoryTreeNode(category, savedCategory);
                },
                error: () => this.enableEditButtons()
            });
    }

    private closeEditDialog(categoryTreeNode: CategoryTreeNode) {
        categoryTreeNode.editMode = null;
    }

    private enableEditButtons() {
        this.isNodeSaving = false;
    }

    private disableEditButtons() {
        this.isNodeSaving = true;
    }

    private updateCategoryTreeNode(node: CategoryTreeNode, category: Category) {
        node.data = category;
        node.label = node.data.name.val;
    }

    private categoryListToCategoryTree(categories: Category[], parent?: Category): Observable<CategoryTreeNode[]> {
        return of(
            categories?.map((category) => {
                category.parentId = parent?.id;
                const children: CategoryTreeNode[] = [];
                this.categoryListToCategoryTree(category.children, category).subscribe((treeNodes) => {
                    children.push(...treeNodes);
                });
                if (this.mode !== 'edit') {
                    for (const externalOperation of category.externalOperations) {
                        if (externalOperation.enabled) {
                            children.push(this.externalOperationToTreeNode(externalOperation));
                        }
                    }
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
            collapsedIcon: 'fa fa-folder',
            expandedIcon: 'fa fa-folder-open',
            expanded: true,
            children: children.sort(this.sortChildren),
            data: category,
            selectable: !this.disabled,
            editMode: null,
            makingRequest: false,
            // We need to add key in the model to provide selection when filter
            // https://github.com/primefaces/primeng/issues/8429#issuecomment-573529483
            key: category.id
        };

        if (this.isFavorite(category) || this.isPreselected(category)) {
            this.selectedResources.push(node);
        }
        this.nodeList.push(node);

        return node;
    }

    private isFavorite(resource: FavoriteResource): boolean {
        return this.favorites?.some((favorite) => favorite.resource?.id === resource.id && favorite.resource?.favoriteType === resource.favoriteType) ?? false;
    }

    private isPreselected(resource: FavoriteResource): boolean {
        return this.preselectedResources?.some((r) => r.id === resource.id && r.favoriteType === resource.favoriteType) ?? false;
    }

    private setLoadingNode(node: CategoryTreeNode, setLoadingChildren = true) {
        node.icon = 'fa fa-spinner fa-spin';
        node.selectable = false;
        if (node.children && setLoadingChildren) {
            for (const child of node.children) {
                this.setLoadingNode(child);
            }
        }
    }

    private unsetLoadingNode(node: CategoryTreeNode, setLoadingChildren = true) {
        if (node.data.favoriteType === 'externalOperation') {
            node.icon = 'fa fa-table';
        } else {
            // @ts-ignore
            node.icon = null;
        }
        node.selectable = !this.disabled;
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
            case 'simpleSelect':
                this.selectionMode = 'checkbox';
                this.propagateSelection = false;
                break;
            case 'select':
                this.selectionMode = 'checkbox';
                this.propagateSelection = true;
                break;
            case 'edit':
                this.selectionMode = 'single';
                this.enableDragAndDrop = true;
                break;
        }
    }

    private reconstructTree(tree: CategoryTreeNode[]) {
        for (const [index, node] of tree.entries()) {
            if (node.data instanceof Category) {
                node.data.index = index;
                node.data.children = node.children!.filter((child) => child.data instanceof Category).map((child) => child.data as Category);
                this.reconstructTree(node.children!);
            }
        }
    }

    private externalOperationToTreeNode(externalOperation: ExternalOperation): CategoryTreeNode {
        const node = {
            label: externalOperation.name.val,
            icon: 'fa fa-table',
            data: externalOperation,
            selectable: !this.disabled,
            draggable: false,
            droppable: false,
            editMode: null,
            makingRequest: false,
            key: externalOperation.code
        };

        if (this.isFavorite(externalOperation) || this.isPreselected(externalOperation)) {
            this.selectedResources.push(node);
        }
        this.nodeList.push(node);

        return node;
    }
}
