import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { NotificationsFilter } from "@app/modules/admin/notifications/notifications-search/notifications-filter";
import { ExternalUser } from "@app/core/model";
import { Receiver } from "@app/shared/model";
import { debounceTime } from "rxjs/operators";
import { NotificationService } from "@app/shared/service";
import { Subject, Subscription } from "rxjs";
import { ArteEventManager } from "arte-ng/services";

@Component({
  selector: 'app-notifications-search',
  templateUrl: './notifications-search.component.html',
  styleUrls: ['./notifications-search.component.css']
})
export class NotificationsSearchComponent implements OnInit, OnDestroy {

    @Input()
    public filters: NotificationsFilter;

    public allowedReceivers: Receiver[] = [];
    public selectedReceivers: Receiver[] = [];

    public subscription: Subscription;
    private filterChangesSubject: Subject<any> = new Subject<any>();

    constructor(private eventManager: ArteEventManager, public notificationService: NotificationService) {

    }

    public ngOnInit() {
        this.subscription = this.filterChangesSubject.pipe(debounceTime(300)).subscribe(() =>
            this.eventManager.broadcast({
                name: 'notificationSearch',
                content: this.filters,
            })
        );
        this.selectedReceivers = this.filters.receivers.map(receiverEmail => {
            let receiver = new Receiver();
            receiver.email = receiverEmail;
            return receiver;
        })
    }

    public searchReceivers(event) {
        this.notificationService.findReceivers({
            query: "EMAIL ILIKE '%" + event.query + "%'"
        }).subscribe((results: Receiver[]) => {
            this.allowedReceivers = results;
        });
    }

    public ngOnDestroy() {
        this.eventManager.destroy(this.subscription);
    }

    public filter(): void {
        this.filterChangesSubject.next();
    }

    public resetFilters(): void {
        this.filters.reset();
        this.selectedReceivers = [];
        this.filter();
    }

    public onSelectReceiver() {
        this.filters.receivers = this.selectedReceivers.map(selectedReceiver => selectedReceiver.email);
        this.filter();
    }

    public onUnselectReceiver() {
        this.filters.receivers = this.selectedReceivers.map(selectedReceiver => selectedReceiver.email);
        this.filter();
    }
}
