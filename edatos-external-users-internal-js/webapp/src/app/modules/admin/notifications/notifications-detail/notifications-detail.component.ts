import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { NotificationService } from "@app/shared/service";
import { Messages, NotificationRecord } from "@app/shared/model";

@Component({
    selector: 'app-notification-details',
    templateUrl: './notifications-detail.component.html'
})
export class NotificationsDetailComponent implements OnInit, OnDestroy {

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
            this.notification = notification;
        });
    }

    private unsubscribeFromNotification() {
        if (this.notificationSubscription) {
            this.notificationSubscription.unsubscribe();
        }
    }

    public joinMessagesText(messages: Messages): string {
        let text = '';
        for (const message of messages.messages) {
            text += message.text;
        }
        return text;
    }
}
