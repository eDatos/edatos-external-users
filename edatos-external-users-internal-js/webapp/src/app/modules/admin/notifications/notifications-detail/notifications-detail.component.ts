import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { NotificationService } from "@app/shared/service";
import { Messages, NotificationRecord } from "@app/shared/model";
import { formatNotification } from "@app/shared/utils/notificationUtils";
import { SafeHtml } from "@angular/platform-browser";

@Component({
    selector: 'app-notification-details',
    templateUrl: './notifications-detail.component.html',
    styleUrls: ['./notifications-detail.component.scss']
})
export class NotificationsDetailComponent implements OnInit, OnDestroy {

    private static MAX_DISPLAYED_RECEIVERS: number = 60;

    public thereAreTooManyReceivers: boolean = false;
    public displayedReceivers: string[] = [];
    public notification: NotificationRecord;
    private notificationSubscription: Subscription;

    constructor(private notificationService: NotificationService, private activatedRoute: ActivatedRoute) {
    }

    ngOnInit() {
        this.subscribeToNotification();
    }

    ngOnDestroy() {
        this.unsubscribeFromNotification();
    }

    private subscribeToNotification() {
        const urn: string = this.activatedRoute.snapshot.params.id;
        this.notificationSubscription = this.notificationService.getNotification(urn).subscribe((notification) => {
            this.notification = formatNotification(notification);
            this.displayedReceivers = this.notification.receivers.slice(0, NotificationsDetailComponent.MAX_DISPLAYED_RECEIVERS);
            this.thereAreTooManyReceivers = this.notification.receivers.length > NotificationsDetailComponent.MAX_DISPLAYED_RECEIVERS;
        });
    }

    private unsubscribeFromNotification() {
        if (this.notificationSubscription) {
            this.notificationSubscription.unsubscribe();
        }
    }

    public getNotificationMessage(): SafeHtml {
        let text = '';
        if (this.notification) {
            for (const message of this.notification.messages.messages) {
                text += message.text;
            }
        }
        return text;
    }

    public toggleDisplayedUsers() {
        if (this.notification.receivers.length === this.displayedReceivers.length) {
            this.displayedReceivers = this.notification.receivers.slice(0, NotificationsDetailComponent.MAX_DISPLAYED_RECEIVERS);
        } else {
            this.displayedReceivers = this.notification.receivers;
        }
    }
}
