import { TreeNode } from "primeng/api";
import { FavoriteResource } from "@app/shared/model";

export interface CategoryTreeNode extends TreeNode {
    data: FavoriteResource;
    parent?: CategoryTreeNode;
    children?: CategoryTreeNode[];
    editMode: 'remap' | 'name' | null;
    makingRequest?: boolean;
}
