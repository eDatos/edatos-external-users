import { BaseVersionedEntity } from 'arte-ng/model';
import { Exclude, Type, TypeOptions } from 'class-transformer';
import { InternationalStringStructuralResources } from './international-string-structural-resources.model';
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

    constructor() {
        super();
        this.type = TypeExternalArtefacts.STATISTICAL_OPERATION;
    }
}

export class ExternalItemCode extends BaseVersionedEntity {
    public id: string;
    public urn: string;

    @Type(() => InternationalStringStructuralResources)
    public name: InternationalStringStructuralResources;

    public type: TypeExternalArtefacts;

    public getLocalisedName(languageCode: string): string | undefined {
        return this.name?.getLocalisedValue(languageCode);
    }
}

export class ExternalCodes {
    public code: ExternalItemCode[];
}
