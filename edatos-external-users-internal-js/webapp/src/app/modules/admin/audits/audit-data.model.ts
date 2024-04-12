export class AuditData {
    constructor(public principalType: string, public remoteAddress: string, public sessionId: string, public code: string, public eventSubtype: string) {}
}
