import { BaseVersionedAndAuditingEntity } from 'arte-ng/model';

export class Notice extends BaseVersionedAndAuditingEntity {
    public messages: Messages[];
    public subject: string;
    public urn: string;
    public sendingDate: Date;

    public messageText: string;

}

export class Messages {
    public messages: Message[];
}


export class Message {
    public text: string;
}
