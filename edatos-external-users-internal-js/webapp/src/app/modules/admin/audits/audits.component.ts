import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LazyLoadEvent } from 'primeng/api';
import { Table, TableService } from 'primeng/table';
import { Audit } from './audit.model';
import { AuditsService } from './audits.service';
import { PAGINATION_OPTIONS } from '@app/app.constants';

@Component({
    selector: 'app-audit',
    templateUrl: './audits.component.html',
    styleUrls: ['./audits.component.scss'],
    providers: [Table, TableService],
})
export class AuditsComponent implements OnInit {
    // Atributos para la paginación
    page: number;
    totalItems: number;
    itemsPerPage: number;
    paginatorOptions = PAGINATION_OPTIONS;

    audits: Audit[];
    fromDate: Date;
    routeData: any;
    orderProp: string;
    reverse: boolean;
    predicate: any;
    toDate: Date;
    today: Date;

    public hiddenFilters: boolean = true;

    public columns: any = [
        {
            fieldName: 'auditEventDate',
            sortable: true,
            header: {
                handler: 'translate',
                translatePath: 'audits.table.header.date',
            },
        },
        {
            fieldName: 'principal',
            sortable: true,
            header: {
                handler: 'translate',
                translatePath: 'audits.table.header.principal',
            },
        },
        {
            fieldName: 'principal_type',
            sortable: true,
            header: {
                handler: 'translate',
                translatePath: 'audits.table.header.principal_type',
            },
        },
        {
            fieldName: 'auditEventType',
            sortable: true,
            header: {
                handler: 'translate',
                translatePath: 'audits.table.header.status',
            },
        },
        {
            fieldName: 'data',
            sortable: true,
            header: {
                handler: 'translate',
                translatePath: 'audits.table.header.data',
            },
        },
    ];

    constructor(private auditsService: AuditsService, private router: Router, private activatedRoute: ActivatedRoute, private datePipe: DatePipe) {
        this.routeData = this.activatedRoute.data.subscribe((data) => {
            this.page = data['pagingParams'].page;
            this.reverse = data['pagingParams'].ascending;
            this.predicate = data['pagingParams'].predicate;
            this.itemsPerPage = data['pagingParams'].itemsPerPage;
        });
    }

    ngOnInit() {
        this.getToday();
        this.previousMonth();
        this.activatedRoute.queryParams.subscribe(() => this.loadAll());
    }

    getToday() {
        const dateFormat = 'yyyy-MM-dd';
        // Today + 1 day - needed if the current day must be included
        const today: Date = new Date();
        today.setDate(today.getDate() + 1);
        const date = new Date(today.getFullYear(), today.getMonth(), today.getDate());
        this.toDate = date;
        this.today = date;
    }

    previousMonth() {
        const dateFormat = 'yyyy-MM-dd';
        let fromDate: Date = new Date();

        if (fromDate.getMonth() === 0) {
            fromDate = new Date(fromDate.getFullYear() - 1, 11, fromDate.getDate());
        } else {
            fromDate = new Date(fromDate.getFullYear(), fromDate.getMonth() - 1, fromDate.getDate());
        }

        this.fromDate = fromDate;
    }

    loadAll() {
        this.auditsService
            .find({
                page: this.page - 1,
                size: this.itemsPerPage,
                sort: this.sort(),
                fromDate: this.dateToString(this.fromDate),
                toDate: this.dateToString(this.toDate),
            })
            .subscribe((res) => {
                this.audits = res.json;
                this.totalItems = +res.headers.get('X-Total-Count')!;
            });
    }

    loadData(e: LazyLoadEvent) {
        this.page = e.first! / e.rows! + 1;
        this.itemsPerPage = e.rows!;
        if (e.sortField != null) {
            this.predicate = e.sortField;
            this.reverse = e.sortOrder == 1;
        }
        this.transition();
    }

    transition() {
        this.router.navigate(['/admin', 'audits'], {
            queryParams: {
                page: this.page,
                size: this.itemsPerPage,
                sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc'),
            },
        });
    }

    onDatesChange() {
        this.page = 1;
        this.transition();
    }

    sort() {
        const sort = this.predicate + ',' + (this.reverse ? 'asc' : 'desc');
        return sort;
    }

    private dateToString(date: Date): string | null {
        const dateFormat = 'yyyy-MM-dd';
        return this.datePipe.transform(date, dateFormat);
    }
}
