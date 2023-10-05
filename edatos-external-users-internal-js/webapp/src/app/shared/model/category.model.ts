import { ExternalCategory, ExternalOperation } from '@app/shared/model/external-item.model';
import { BaseVersionedAndAuditingEntity } from 'arte-ng/model';
import { Exclude, Type } from 'class-transformer';
import { InternationalString } from './international-string.model';

export class Category extends BaseVersionedAndAuditingEntity {
    @Exclude()
    public readonly favoriteType = 'category';

    public subscribers = 0;
    public index = 0;

    @Type(() => InternationalString)
    public name = new InternationalString();

    @Type(() => Category)
    public children: Category[] = [];

    @Type(() => ExternalCategory)
    public externalCategories: ExternalCategory[] = [];

    @Type(() => ExternalOperation)
    public externalOperations: ExternalOperation[] = [];

    public parentId: number = 0;
}
