import { BaseVersionedAndAuditingEntity } from 'arte-ng/model';
import { Type } from 'class-transformer';
import { ExternalOperation } from './external-item.model';
import { User } from '@app/core/model';

export class Filter extends BaseVersionedAndAuditingEntity {
    public name: string;
    public resourceName: string | null = null;
    public permalink: string;
    public urlPermalink: string;
    public lastAccessDate: Date;
    public notes: string | null = null;

    @Type(() => User)
    public externalUser: User;

    @Type(() => ExternalOperation)
    public externalOperation: ExternalOperation;
}
