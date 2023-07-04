import { BaseVersionedEntity } from 'arte-ng/model';
import { Exclude, Type } from 'class-transformer';
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

    public get schema(): { code?: string; version?: string } {
        const regex = /^urn:(?:\w+:)?(?:\w+\.)+\w+=\w+:(?<schemaCode>\w+)\((?<schemaVersion>.+?)\)/.exec(this.urn);
        return {
            code: regex?.groups?.schemaCode,
            version: regex?.groups?.schemaVersion
        };
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
