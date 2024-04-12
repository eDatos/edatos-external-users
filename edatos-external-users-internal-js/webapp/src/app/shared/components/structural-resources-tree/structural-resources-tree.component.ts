import { OnInit } from '@angular/core';
import { Category, ExternalOperation } from '@app/shared/model';
import { Observable, of } from 'rxjs';
import { CategoryTreeNode } from "@app/shared/components/structural-resources-tree/category-tree-node.model";
import { CategoryService } from "@app/shared/service";

export abstract class StructuralResourcesTreeComponent implements OnInit {

    public tree: CategoryTreeNode[];

    protected constructor(protected categoryService: CategoryService, protected showOperations) {

    }

    public ngOnInit(): void {
        this.getAndCreateTree();
    }

    protected getAndCreateTree() {
        this.categoryService.getTree().subscribe((tree) => {
            this.createTree(tree);
        });
    }

    protected createTree(tree: Category[]) {
        this.categoryListToCategoryTree(tree).subscribe((treeNodes) => {
            this.tree = treeNodes;
            this.afterTreeCreation();
        });
    }

    protected afterTreeCreation() {

    }

    protected categoryListToCategoryTree(categories: Category[], parent?: Category): Observable<CategoryTreeNode[]> {
        return of(
            categories?.map((category) => {
                category.parentId = parent?.id;
                const children: CategoryTreeNode[] = [];
                this.categoryListToCategoryTree(category.children, category).subscribe((treeNodes) => {
                    children.push(...treeNodes);
                });
                if (this.showOperations) {
                    for (const externalOperation of category.externalOperations) {
                        if (externalOperation.enabled) {
                            children.push(this.externalOperationToTreeNode(externalOperation));
                        }
                    }
                }
                return this.categoryToCategoryTreeNode(category, children);
            }).sort(this.sortChildren)
        );
    }

    protected externalOperationToTreeNode(externalOperation: ExternalOperation): CategoryTreeNode {
        return {
            label: externalOperation.name.val,
            icon: 'fa fa-table',
            data: externalOperation,
            draggable: false,
            droppable: false,
            editMode: null,
            makingRequest: false,
            key: externalOperation.code
        };
    }

    protected categoryToCategoryTreeNode(category: Category, children: CategoryTreeNode[]): CategoryTreeNode {
        return {
            label: category.name.val,
            collapsedIcon: 'fa fa-folder',
            expandedIcon: 'fa fa-folder-open',
            expanded: true,
            children: children,
            data: category,
            editMode: null,
            makingRequest: false,
            // We need to add key in the model to provide selection when filter
            // https://github.com/primefaces/primeng/issues/8429#issuecomment-573529483
            key: category.id
        };
    }

    protected sortChildren(a, b): -1 | 1 {
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
}
