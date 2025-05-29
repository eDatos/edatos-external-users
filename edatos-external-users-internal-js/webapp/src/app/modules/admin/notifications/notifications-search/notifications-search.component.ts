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
        this.searchReceivers();
    }

    private searchReceivers() {
        this.notificationService.findReceivers().subscribe((results: Receiver[]) => {
            this.allowedReceivers = results;
            this.selectedReceivers = this.allowedReceivers.filter(receiver => this.filters.receivers.includes(receiver.email));
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

    public onSelectReceiver(receiver: ExternalUser) {
        this.selectedReceivers.push(receiver);
        this.filters.receivers = this.selectedReceivers.map(selectedReceiver => selectedReceiver.email);
        this.filter();
    }

    public onUnselectReceiver(receiver: ExternalUser) {
        const index = this.selectedReceivers.indexOf(receiver);
        if (index > -1) {
            this.selectedReceivers.splice(index, 1);
        }
        this.filters.receivers = this.selectedReceivers.map(selectedReceiver => selectedReceiver.email);
        this.filter();
    }
}
