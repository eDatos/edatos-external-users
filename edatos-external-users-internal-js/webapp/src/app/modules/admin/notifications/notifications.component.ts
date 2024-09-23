import { Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ExternalUser } from '@app/core/model';
import { ExternalOperation, Notification, Receiver, RequiredLocalesEnum } from '@app/shared/model';
import { errorErrorMessage } from '@app/core/service/alert/eusuarios-alert.service';
import { MultiLanguageInputComponent } from '@app/shared/components/multi-language-input';
import { CategoryService, ExternalOperationService, LanguageService, NotificationService } from '@app/shared/service';
import { TranslateService } from '@ngx-translate/core';
import { ArteEventManager } from 'arte-ng/services';
import { MessageService } from 'primeng/api';
import { Observable, Subscription } from 'rxjs';
import { AutocompleteShortListComponent } from 'arte-ng';
import { FavoriteResource, InternationalString } from '@app/shared/model';
import { ExternalUserFilter } from '@app/modules/external-user/external-user-search/external-user-filter';
import * as _ from 'lodash';
import { shareReplay } from 'rxjs/operators';
import { StructuralResourcesTreeSimpleSelectComponent } from "@app/shared/components/structural-resources-tree/structural-resources-tree-simple-select/structural-resources-tree-simple-select.component";
import { MultiLanguageEditorComponent } from "@app/shared/components/multi-language-editor/multi-language-editor.component";

@Component({
    selector: 'app-notifications',
    templateUrl: './notifications.component.html',
    styleUrls: ['./notifications.component.scss']
})
export class NotificationsComponent implements OnInit, OnDestroy {

    public ALL_LOCALES = RequiredLocalesEnum.ALL_LOCALES;

    @Input()
    public filters: ExternalUserFilter = new ExternalUserFilter();

    public subscription: Subscription;

    public allowedReceivers: Receiver[] = [];
    public notification: Notification;
    public selectedReceivers: string[] = [];

    public allowedFiltersExternalOperations: ExternalOperation[] = [];
    public selectedFiltersExternalOperations: ExternalOperation[] = [];

    public isSelectingResource = false;
    public selectedResources: FavoriteResource[] = [];

    public failedReceivers = '';
    public thereAreFailedReceivers = false;

    @ViewChild(AutocompleteShortListComponent)
    public selectedReceiversComponent: AutocompleteShortListComponent;

    @ViewChild(StructuralResourcesTreeSimpleSelectComponent)
    public tree: StructuralResourcesTreeSimpleSelectComponent;

    @ViewChild(MultiLanguageInputComponent)
    public languageInputComponent: MultiLanguageInputComponent;
    @ViewChild(MultiLanguageEditorComponent)
    public languageEditorComponent: MultiLanguageEditorComponent;
    public isSubjectModalVisible = false;
    public isMessageModalVisible = false;

    public availableLanguages: string[] = [];
    public disableValueLanguageInput = true;
    public addedMessageValueLanguageInput = false;
    public addedSubjectValueLanguageInput = false;

    public isSending = false;

    constructor(
        private eventManager: ArteEventManager,
        public notificationService: NotificationService,
        public messageService: MessageService,
        public translateService: TranslateService,
        private categoryService: CategoryService,
        private languageService: LanguageService,
        private externalOperationService: ExternalOperationService
    ) {
    }

    public ngOnInit() {
        this.notification = new Notification();
        this.searchReceivers();

        this.categoryService.findAll({ size: 100000000 }).subscribe((response) => {
            this.selectedResources = response.body.filter((category) => this.filters.categories.includes(category.id.toString()));
            for (const category of response.body) {
                this.selectedResources.push(...category.externalOperations.filter((externalOp) => this.filters.externalOperations.includes(externalOp.id.toString())));
            }
        });

        this.languageService
            .getInternationalLanguages()
            .pipe(shareReplay({ bufferSize: 1, refCount: true }))
            .subscribe((languages) => (this.availableLanguages = languages));

        this.externalOperationService.find({size: 10000}).subscribe((response) => {
            this.allowedFiltersExternalOperations = response.body.sort((operation1, operation2) => {
                return operation1.code.toLowerCase() < operation2.code.toLowerCase() ? -1 : 1;
            });
        });
    }

    public ngOnDestroy() {
        if (this.subscription) {
            this.eventManager.destroy(this.subscription);
        }
    }

    public send() {
        this.notification.categoriesIds = this.arrStringToArrNumber(this.filters.categories);
        this.notification.externalOperationsIds = this.arrStringToArrNumber(this.filters.externalOperations);
        this.notification.filtersExternalOperationsIds = this.arrStringToArrNumber(this.filters.filtersExternalOperations);
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

    public onSelectFiltersExternalOperations(operation: ExternalOperation) {
        this.selectedFiltersExternalOperations.push(operation);
        this.filters.filtersExternalOperations = this.selectedFiltersExternalOperations.map(operation => operation.id.toString());
    }

    public onUnselectFiltersExternalOperations(operation: ExternalOperation) {
        const index = this.selectedFiltersExternalOperations.indexOf(operation);
        if (index > -1) {
            this.selectedFiltersExternalOperations.splice(index, 1);
        }
        this.filters.filtersExternalOperations = this.selectedFiltersExternalOperations.map(operation => operation.id.toString());
    }

    public saveSubjectText(notice: Notification, subject: InternationalString) {
        notice.subject = subject;
        this.isSubjectModalVisible = false;
        this.addedSubjectValueLanguageInput = true;
    }

    public validateMultiLanguageText(value: InternationalString) {
        this.disableValueLanguageInput = value.texts.some((text) => this.isRichTextEmpty(text.label))
            || this.availableLanguages.some(language => !value.texts.some(text => text.locale === language));
    }

    private isRichTextEmpty(text?: string | null): boolean {
        return text === null || text === undefined || !this.removeHtmlTags(text);
    }

    private removeHtmlTags(text: string): string {
        const domParser = new DOMParser();
        return domParser.parseFromString(text, 'text/html').body.textContent?.trim() || '';
    }

    public saveMessageText(notice: Notification, message: InternationalString) {
        notice.message = message;
        this.isMessageModalVisible = false;
        this.addedMessageValueLanguageInput = true;
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

    public resetFiltersExternalOperations() {
        this.selectedFiltersExternalOperations = [];
        this.filters.filtersExternalOperations = [];
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
        return (this.selectedReceivers && this.selectedReceivers.length) ||
            (this.filters.categories && this.filters.categories.length) ||
            (this.filters.externalOperations && this.filters.externalOperations.length) ||
            (this.filters.filtersExternalOperations && this.filters.filtersExternalOperations.length);
    }

    public isDisabledForSend() {
        return !this.thereAreReceivers() || !this.thereAreContentMail();
    }

    public thereAreContentMail() {
        return (this.notification.subject && this.notification.subject.texts.length) && (this.notification.message && this.notification.message.texts.length);
    }

    public trackSelectedResource(index: number, item: FavoriteResource) {
        return `${item.id}${item.favoriteType}`;
    }

    public openCategoryModal() {
        this.isSelectingResource = true;
    }

    public closeCategoryModal() {
        this.isSelectingResource = false;
    }

    public openSubjectModal() {
        this.validateMultiLanguageText(this.notification.subject);
        this.isSubjectModalVisible = true;
    }

    public closeSubjectModal() {
        this.isSubjectModalVisible = false;
    }

    public openMessageModal() {
        this.validateMultiLanguageText(this.notification.message);
        this.isMessageModalVisible = true;
    }

    public closeMessageModal() {
        this.isMessageModalVisible = false;
    }

    public closeFailedReceivers() {
        this.thereAreFailedReceivers = false;
    }

    private arrStringToArrNumber(arrString: string[]) {
        return arrString.map(str => {
            return Number(str);
        });
    }

    private subscribeToSendResponse(result: Observable<Notification>) {
        this.isSending = true;
        result.subscribe(
            (response: Notification) => {
                this.onSaveSuccess();
                this.thereAreFailedReceivers = false;
                this.selectedReceivers = [];
                this.selectedReceiversComponent.writeValue([]);
                this.notification = new Notification();
                this.resetCategories();
                this.resetFiltersExternalOperations();
                this.addedSubjectValueLanguageInput = false;
                this.addedMessageValueLanguageInput = false;
                this.isSending = false;
            },
            () => {
                this.thereAreFailedReceivers = true;
                this.failedReceivers = errorErrorMessage['failedReceivers']?.map((receiver) => receiver).join(', ');
                this.isSending = false;
                this.onSaveError();
            }
        );
    }

    private searchReceivers() {
        this.notificationService.findReceivers().subscribe((results) => {
            this.allowedReceivers = results;
        });
    }

    private onSaveSuccess() {
        this.messageService.add({
            key: 'customAlertKey',
            severity: 'success',
            summary: this.translateService.instant('notifications.message.onSuccessSumary'),
            detail: this.translateService.instant('notifications.message.onSuccess'),
            life: 5000
        });
    }

    private onSaveError() {
        this.messageService.add({
            key: 'customAlertKey',
            severity: 'error',
            summary: this.translateService.instant('notifications.message.onErrorSumary'),
            detail: this.translateService.instant('notifications.message.onError'),
            life: 5000
        });
    }
}
