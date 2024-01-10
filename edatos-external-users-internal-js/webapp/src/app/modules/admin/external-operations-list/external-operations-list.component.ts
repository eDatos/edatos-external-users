import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ITEMS_PER_PAGE, PAGINATION_OPTIONS } from '@app/app.constants';
import { ResponseWrapper } from '@app/core/utils/response-utils';
import { ExternalOperation } from '@app/shared/model';
import { ExternalOperationService } from '@app/shared/service';
import { ArteEventManager, GenericModalService } from 'arte-ng/services';
import { LazyLoadEvent } from 'primeng/api';
import { ConfirmationModal } from "@app/shared/components/confirmation-modal/confirmation-modal.component";

@Component({
    selector: 'app-external-operations-list',
    templateUrl: './external-operations-list.component.html',
    styleUrls: ['./external-operations-list.component.scss'],
})
export class ExternalOperationsListComponent implements OnInit {
    public totalItems: number | null;
    public itemsPerPage = ITEMS_PER_PAGE;
    public paginatorOptions = PAGINATION_OPTIONS;
    public page = 1;
    public externalOperations: ExternalOperation[];
    public predicate: string;
    public reverse: boolean;
    public columns = [
        {
            fieldName: 'code',
            sortable: false,
            header: {
                handler: 'translate',
                translatePath: 'entity.code',
            },
        },
        {
            fieldName: 'name',
            sortable: false,
            header: {
                handler: 'translate',
                translatePath: 'entity.name',
            },
        },
        {
            fieldName: 'publication_date',
            sortable: false,
            header: {
                handler: 'translate',
                translatePath: 'entity.publicationDate',
            },
        },
        {
            fieldName: 'status',
            header: {
                handler: 'translate',
                translatePath: 'entity.status',
            },
        },
    ];

    constructor(
        private eventManager: ArteEventManager,
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private externalOperationService: ExternalOperationService,
        private genericModalService: GenericModalService
    ) {}

    public ngOnInit() {
        this.loadAll();
    }

    public loadAll() {
        const queryRequestParams = {
            page: this.page ? this.page - 1 : 0,
            size: this.itemsPerPage || ITEMS_PER_PAGE,
            sort: ['publication_date,asc', 'id'],
        };
        this.externalOperationService.find(queryRequestParams).subscribe((res) => this.onSuccess(res));
    }

    public loadData(e: LazyLoadEvent) {
        this.page = e.first! / e.rows! + 1;
        this.itemsPerPage = e.rows!;
        if (e.sortField != null) {
            this.predicate = e.sortField;
            this.reverse = e.sortOrder === 1;
        }
        this.loadAll();
    }

    public changeStateOfExternalOperation(externalOperation: ExternalOperation): void {
        var modalData;
        if (!externalOperation.enabled) {
            modalData = {
                headerText: "externalOperation.disablementConfirmationModal.title",
                bodyText: "externalOperation.disablementConfirmationModal.body",
                bodyParams: { operation: externalOperation.code },
                isDangerous: true
            };
        } else {
            modalData = {
                headerText: "externalOperation.enablementConfirmationModal.title",
                bodyText: "externalOperation.enablementConfirmationModal.body",
                bodyParams: { operation: externalOperation.code }
            };
        }
        const modalRef = this.genericModalService.open(ConfirmationModal as Component, modalData, { container: '.app' });
        modalRef.result.subscribe((isConfirmed) => {
            if (isConfirmed) {
                this.updateExternalOperation(externalOperation);
            } else {
                externalOperation.enabled = !externalOperation.enabled;
            }
        });
    }

    public updateExternalOperation(externalOperation: ExternalOperation): void {
        this.externalOperationService.update(externalOperation).subscribe((externalOp) => {
            Object.assign(externalOperation, externalOp);
        });
    }

    private onSuccess(data: ResponseWrapper<ExternalOperation[]>) {
        this.totalItems = data.totalCount();
        this.externalOperations = data.body;
    }
}
