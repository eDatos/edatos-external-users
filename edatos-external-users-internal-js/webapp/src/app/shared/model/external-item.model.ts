import { BaseVersionedEntity } from 'arte-ng/model';
import { Exclude, Type, TypeOptions } from 'class-transformer';
import { InternationalStringV1 } from './international-string-v1.model';
import { InternationalString } from './international-string.model';

/**
 * This enum is a subset from the one located at
 * {@link
 * https://git.arte-consultores.com/istac/edatos-core-common/blob/master/src/main/java/org/siemac/edatos/core/common/enume/TypeExternalArtefactsEnum.java|edatos-core-common}.
 */
export enum TypeExternalArtefacts {
    CATEGORY = 'CATEGORY',
    STATISTICAL_OPERATION = 'STATISTICAL_OPERATION',
}

export class ExternalItem extends BaseVersionedEntity {
    public id: number;
    public code: string;
    public urn: string;

    @Type(() => InternationalString)
    public name: InternationalString;

    public type: TypeExternalArtefacts;

    public getLocalisedName(languageCode: string): string | undefined {
        return this.name?.getLocalisedLabel(languageCode);
    }
}

export class ExternalCategory extends ExternalItem {
    public nestedCode: string;

    constructor() {
        super();
        this.type = TypeExternalArtefacts.CATEGORY;
    }
}

export class ExternalOperation extends ExternalItem {
    @Exclude()
    public readonly favoriteType = 'externalOperation';

    public externalCategoryUrn: string;
    public subscribers = 0;
    public numberOfFilters = 0;
    public enabled = false;
    public notificationsEnabled = true;
    public publicationDate: Date;

    constructor() {
        super();
        this.type = TypeExternalArtefacts.STATISTICAL_OPERATION;
    }
}

export class ExternalItemCode extends BaseVersionedEntity {
    public id: string;
    public urn: string;

    @Type(() => InternationalStringV1)
    public name: InternationalStringV1;

    public type: TypeExternalArtefacts;

    public getLocalisedName(languageCode: string): string | undefined {
        return this.name?.getLocalisedLabel(languageCode);
    }
}

export class ExternalCodes {
    public code: ExternalItemCode[];
}

/**
 * This configuration object inform class-transformer how to instantiate the
 * ExternalItem to the right subclass, if necessary.
 */
export const EXTERNAL_ITEM_DISCRIMINATOR: TypeOptions = {
    discriminator: {
        property: 'type',
        subTypes: [
            { value: ExternalCategory, name: TypeExternalArtefacts.CATEGORY },
            { value: ExternalOperation, name: TypeExternalArtefacts.STATISTICAL_OPERATION },
        ],
    },
    keepDiscriminatorProperty: true,
};
