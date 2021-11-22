import { Component, OnDestroy, OnInit } from '@angular/core';
import { ExternalUser, Notification } from '@app/core/model';
import { ExternalUserService } from '@app/core/service';
import { NotificationService } from '@app/shared/service';
import { TranslateService } from '@ngx-translate/core';
import { ArteEventManager } from 'arte-ng/services';
import { MessageService } from 'primeng/api';
import { Observable, Subscription } from 'rxjs';

@Component({
    selector: 'app-notifications',
    templateUrl: './notifications.component.html',
})
export class NotificationsComponent implements OnInit, OnDestroy {
    public subscription: Subscription;

    public externalUsers: ExternalUser[] = [];
    public notification: Notification;
    public selectedReceivers: string[] = [];

    constructor(
        private eventManager: ArteEventManager,
        public notificationService: NotificationService,
        public externalUserService: ExternalUserService,
        public messageService: MessageService,
        public translateService: TranslateService
    ) {}

    public ngOnInit() {
        this.notification = new Notification();
        this.searchExternalUsers();
    }

    public ngOnDestroy() {
        if (this.subscription) {
            this.eventManager.destroy(this.subscription);
        }
    }

    public send() {
        this.notification.receivers = this.selectedReceivers;
        this.subscribeToSendResponse(this.notificationService.send(this.notification));
    }

    public onSelectReceivers(receivers: ExternalUser) {
        this.selectedReceivers.push(receivers.email);
    }

    public onUnselectReceivers(receivers) {
        const index = this.selectedReceivers.indexOf(receivers.email);
        if (index > -1) {
            this.selectedReceivers.splice(index, 1);
        }
    }

    private subscribeToSendResponse(result: Observable<Notification>) {
        result.subscribe(
            (res: Notification) => this.onSaveSuccess(),
            (res: Response) => this.onSaveError()
        );
    }

    private searchExternalUsers() {
        this.externalUserService.find().subscribe((results) => {
            this.externalUsers = results.body;
        });
    }

    private onSaveSuccess() {
        this.messageService.add({
            key: 'customAlertKey',
            severity: 'success',
            summary: this.translateService.instant('notifications.message.onSuccessSumary'),
            detail: this.translateService.instant('notifications.message.onSuccess'),
            life: 5000,
        });
    }

    private onSaveError() {}
}
