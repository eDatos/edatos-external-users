import { Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ExternalUser, Notification } from '@app/core/model';
import { ExternalUserService } from '@app/core/service';
import { CategoryService, NotificationService } from '@app/shared/service';
import { TranslateService } from '@ngx-translate/core';
import { ArteEventManager } from 'arte-ng/services';
import { MessageService } from 'primeng/api';
import { Observable, Subject, Subscription} from 'rxjs';
import { AutocompleteShortListComponent } from 'arte-ng';
import { FavoriteResource } from '@app/shared/model';
import { StructuralResourcesTreeComponent } from '@app/shared/components/structural-resources-tree';
import { ExternalUserFilter } from '@app/modules/external-user/external-user-search/external-user-filter';
import * as _ from 'lodash';

@Component({
    selector: 'app-notifications',
    templateUrl: './notifications.component.html',
    styleUrls: ['./notifications.component.scss'],
})
export class NotificationsComponent implements OnInit, OnDestroy {

    @Input()
    public filters: ExternalUserFilter = new ExternalUserFilter();

    public subscription: Subscription;

    public externalUsers: ExternalUser[] = [];
    public notification: Notification;
    public selectedReceivers: string[] = [];

    public isSelectingResource = false;
    public selectedResources: FavoriteResource[] = [];

    @ViewChild(AutocompleteShortListComponent)
    public selectedReceiversComponent: AutocompleteShortListComponent;

    @ViewChild(StructuralResourcesTreeComponent)
    public tree: StructuralResourcesTreeComponent;

    constructor(
        private eventManager: ArteEventManager,
        public notificationService: NotificationService,
        public externalUserService: ExternalUserService,
        public messageService: MessageService,
        public translateService: TranslateService,
        private categoryService: CategoryService
    ) {}

    public ngOnInit() {
        this.notification = new Notification();
        this.searchExternalUsers();

        this.categoryService.findAll({ size: 100000000 }).subscribe((response) => {
            this.selectedResources = response.body.filter((category) => this.filters.categories.includes(category.id.toString()));
            for (const category of response.body) {
                this.selectedResources.push(...category.externalOperations.filter((externalOp) => this.filters.externalOperations.includes(externalOp.id.toString())));
            }
        });
    }

    public ngOnDestroy() {
        if (this.subscription) {
            this.eventManager.destroy(this.subscription);
        }
    }

    public send() {
        console.log('CATEGORIES ',this.filters.categories);
        this.notification.categoriesIds = this.arrStringToArrNumber(this.filters.categories);
        this.notification.externalOperationsIds = this.arrStringToArrNumber(this.filters.externalOperations);
        this.notification.receivers = this.selectedReceivers;
        this.subscribeToSendResponse(this.notificationService.send(this.notification));
    }

    private arrStringToArrNumber(arrString: string[]) {
        return arrString.map(str => {
            return Number(str);
          });
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
            (res: Notification) => {
                this.onSaveSuccess();
                this.selectedReceivers = [];
                this.selectedReceiversComponent.writeValue([]);
                this.notification = new Notification();
                this.resetCategories();
            },
            (res: Response) => {
                this.onSaveError();
            }
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

    public openCategoryModal() {
        this.isSelectingResource = true;
    }

    public closeCategoryModal() {
        this.isSelectingResource = false;
    }

    public selectCategories() {
        const categoriesIds: string[] = [];
        const externalOperationsIds: string[] = [];
        for (const resource of this.tree.selectedResources) {
            if (resource.data.favoriteType === 'category') {
                categoriesIds.push(resource.data.id.toString());
            } else {
                externalOperationsIds.push(resource.data.id.toString());
            }
        }
        this.filters.categories = categoriesIds;
        this.filters.externalOperations = externalOperationsIds;
        this.updateSelectedResources();
        this.closeCategoryModal();
    }

    public resetCategories() {
        this.filters.categories = [];
        this.filters.externalOperations = [];
        this.selectedResources = [];
    }

    public updateSelectedResources() {
        this.selectedResources = this.tree.selectedResources.map((node) => node.data);
    }

    public deleteResource(resource: FavoriteResource) {
        const filter = resource.favoriteType === 'category' ? this.filters.categories : this.filters.externalOperations;
        _.pull(this.selectedResources, resource);
        _.pull(filter, resource.id.toString());
    }

    public thereAreReceivers() {
        return (this.selectedReceivers  && this.selectedReceivers.length) ||
            (this.filters.categories  && this.filters.categories.length) ||
            (this.filters.externalOperations  && this.filters.externalOperations.length);
    }

    public trackSelectedResource(index: number, item: FavoriteResource) {
        return `${item.id}${item.favoriteType}`;
    }
}
