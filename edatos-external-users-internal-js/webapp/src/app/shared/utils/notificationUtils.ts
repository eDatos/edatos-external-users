import { NotificationRecord } from "@app/shared/model";

export function getFormattedNotificationSubject(subject: string): string {
    // Removing the name of the app at the beginning of the subject
    return subject.replace(/^\[[^\]]+] */, "");
}

export function formatNotification(notificationRecord: NotificationRecord): NotificationRecord {
    notificationRecord.subject = getFormattedNotificationSubject(notificationRecord.subject);
    return notificationRecord;
}

export function formatNotifications(notificationRecords: NotificationRecord[]): NotificationRecord[] {
    return notificationRecords.map(formatNotification);
}
