import { ExternalUser } from '@app/core/model';
import { ExternalOperation } from '@app/shared/model/external-item.model';
import { BaseVersionedAndAuditingEntity } from 'arte-ng/model';
import { Type } from 'class-transformer';
import { Category } from './category.model';

export type FavoriteResource = Category | ExternalOperation;

export class Favorite extends BaseVersionedAndAuditingEntity {
    @Type(() => ExternalUser)
    public externalUser: ExternalUser;

    @Type(() => Category)
    private category: Category | null;

    @Type(() => ExternalOperation)
    private externalOperation: ExternalOperation | null;

    // @ts-ignore
    public get resource(): FavoriteResource | null {
        return this.category || this.externalOperation;
    }

    // @ts-ignore
    public set resource(resource: FavoriteResource) {
        if (resource.favoriteType === 'category') {
            this.category = resource;
            this.externalOperation = null;
        } else if (resource.favoriteType === 'externalOperation') {
            this.externalOperation = resource;
            this.category = null;
        } else {
            throw new Error('Unrecognized favorite type');
        }
    }

    public get favoriteType(): 'category' | 'externalOperation' | undefined {
        return this.resource?.favoriteType;
    }
}
