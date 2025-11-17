import { BaseVersionedAndAuditingEntity } from 'arte-ng/model';
import { Type } from 'class-transformer';
import { User } from './user.model';
import { ExternalOperation } from '@app/shared';

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
