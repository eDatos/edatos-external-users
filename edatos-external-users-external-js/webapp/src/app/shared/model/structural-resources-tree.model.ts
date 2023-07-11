import { Type } from 'class-transformer';
import { InternationalString } from './international-string.model';

export class StructuralResourcesTree {
    public id: number | null = null;
    public code: string | null = null;
    public type: 'category' | 'externalOperation';

    @Type(() => InternationalString)
    public name: InternationalString;

    @Type(() => StructuralResourcesTree)
    public children: StructuralResourcesTree[] = [];

    public index: number = 0;
}
