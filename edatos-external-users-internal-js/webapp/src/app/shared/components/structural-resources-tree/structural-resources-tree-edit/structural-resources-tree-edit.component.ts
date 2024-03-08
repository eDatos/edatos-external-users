import { Component, ViewChild } from '@angular/core';
import { Category, ExternalCategory, InternationalString } from "@app/shared/model";
import { StructuralResourcesTreeComponent } from "@app/shared/components/structural-resources-tree/structural-resources-tree.component";
import { CategoryTreeNode } from "@app/shared/components/structural-resources-tree/category-tree-node.model";
import { CategoryService, LanguageService } from "@app/shared/service";
import { DEFAULT_LOCALE } from '@app/shared/components/multi-language-input/multi-language-input.constants';
import { finalize, shareReplay } from "rxjs/operators";
import { MultiLanguageInputComponent } from "@app/shared/components/multi-language-input";
import * as _ from "lodash";

// TODO check last changes on the tree
@Component({
  selector: 'app-structural-resources-tree-edit',
  templateUrl: './structural-resources-tree-edit.component.html',
  styleUrls: ['./structural-resources-tree-edit.component.css']
})
export class StructuralResourcesTreeEditComponent extends StructuralResourcesTreeComponent {

    public DEFAULT_LOCALE = DEFAULT_LOCALE;

    public loading = false;
    public isNodeSaving = false;
    public allowedLanguages: string[];
    public clickedDelete: boolean;

    @ViewChild(MultiLanguageInputComponent)
    public languageInputComponent: MultiLanguageInputComponent;

    constructor(categoryService: CategoryService, private languageService: LanguageService) {
        super(categoryService, false);
        this.languageService
            .getAllowed()
            .pipe(shareReplay({ bufferSize: 1, refCount: true }))
            .subscribe((languages) => (this.allowedLanguages = languages));
    }

    // CREATE OR EDIT CATEGORY NODE

    public addNode(parent?: CategoryTreeNode) {
        const category = new Category();
        const node: CategoryTreeNode = {
            label: '',
            collapsedIcon: 'fa fa-folder',
            expandedIcon: 'fa fa-folder-open',
            expanded: true,
            data: category,
            children: [],
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
        this.editNodeName(node);
    }

    public editNodeName(node: CategoryTreeNode) {
        node.editMode = 'name';
    }

    public saveNodeName(node: CategoryTreeNode, name: InternationalString) {
        this.disableEditButtons();
        if (node.data.id) {
            this.updateCategoryName(node, name);
        } else {
            this.createCategoryName(node, name);
        }
    }

    private disableEditButtons() {
        this.isNodeSaving = true;
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

    private enableEditButtons() {
        this.isNodeSaving = false;
    }

    private updateCategoryTreeNode(node: CategoryTreeNode, category: Category) {
        node.data = category;
        node.label = node.data.name.val;
        if (node.parent?.data instanceof Category) {
            node.parent.data.children = node.parent.data.children.map(child => child.id ? child : category);
        }
    }

    public cancelNodeNameEdit(node: CategoryTreeNode) {
        if (node.data.name.isEmptyOrBlank()) {
            this.deleteNode(node);
        }
        this.enableEditButtons();
        this.closeEditDialog(node);
    }

    // DELETE CATEGORY NODE

    public deleteNode(node: CategoryTreeNode) {
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
                        this.getAndCreateTree();
                        this.loading = false;
                    })
                )
                .subscribe();
        }
    }

    // MOVE CATEGORY NODE

    public onNodeDrop(event: { dragNode: CategoryTreeNode; dropNode: CategoryTreeNode } & ({ index: number } | { dropIndex: number })) {
        this.loading = true;
        this.reconstructTree(this.tree);
        this.categoryService
            .updateTree(this.tree.map((node) => node.data as Category))
            .pipe(finalize(() => (this.loading = false)))
            .subscribe({
                next: (tree) => this.createTree(tree),
                error: () => this.getAndCreateTree(),
            });
    }

    private reconstructTree(tree: CategoryTreeNode[], parent?: CategoryTreeNode) {
        for (const [index, node] of tree.entries()) {
            if (node.data instanceof Category) {
                node.data.index = index;
                node.data.children = node.children!.filter((child) => child.data instanceof Category).map((child) => child.data as Category);
                node.data.parentId = parent?.data.id;
                node.parent = parent;
                this.reconstructTree(node.children!, node);
            }
        }
    }

    // REMAP EXTERNAL CATEGORIES

    public remapNode(node: CategoryTreeNode) {
        node.editMode = 'remap';
    }

    public getDisabledExternalCategories(selectedCategory: Category): ExternalCategory[] {
        const externalCategoriesAlreadySelectedByOtherNode: ExternalCategory[] = [];
        const categoryNodes = [...this.tree];
        for (const node of categoryNodes) {
            if (node.children) {
                categoryNodes.push(...node.children.filter(child => child.data.favoriteType === "category"));
            }
            if (node.data.id !== selectedCategory.id) {
                externalCategoriesAlreadySelectedByOtherNode.push(...(node.data as Category).externalCategories);
            }
        }
        return externalCategoriesAlreadySelectedByOtherNode;
    }

    public saveRemap(node: CategoryTreeNode, selectedResources: ExternalCategory[]) {
        if (node.data instanceof Category) {
            node.data.externalCategories = selectedResources;
        }
        this.closeEditDialog(node);
        this.updateTree();
    }

    // OTHERS

    public closeEditDialog(categoryTreeNode: CategoryTreeNode) {
        categoryTreeNode.editMode = null;
    }

    private updateTree() {
        this.categoryService
            .updateTree(this.tree.map((node) => node.data as Category))
            .pipe(finalize(() => (this.loading = false)))
            .subscribe({
                next: (tree) => this.createTree(tree)
            });
    }
}
