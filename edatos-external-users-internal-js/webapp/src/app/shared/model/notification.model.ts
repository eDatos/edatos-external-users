import { InternationalString } from './international-string.model';
import { Type } from 'class-transformer';
import { BaseVersionedAndAuditingEntity } from "arte-ng/model";

export class Notification {
    @Type(() => InternationalString)
    public subject: InternationalString = new InternationalString();

    @Type(() => InternationalString)
    public message: InternationalString = new InternationalString();

    public receivers?: string[];
    public categoriesIds?: number[];
    public externalOperationsIds?: number[];
    public filtersExternalOperationsIds?: number[];
}

export class NotificationRecord extends BaseVersionedAndAuditingEntity {
    public messages: Messages;
    public subject: string;
    public urn: string;
    public sendingDate: Date;
}

export class Messages {
    public messages: Message[];
}


export class Message {
    public text: string;
}
