import { Params } from '@angular/router';
import { BaseEntityFilter, EntityFilter } from 'arte-ng/model';
import { DatePipe } from "@angular/common";

export class NotificationsFilter extends BaseEntityFilter implements EntityFilter {
    public subject: string | null;
    public date: Date[] | null = null; // null because in this version of Primeng an empty array cause an error in the initialization of the p-calendar
    public receivers: string[] = [];

    constructor(datePipe: DatePipe) {
        super(datePipe);
    }

    public processUrlParams(queryParams: Params) {
        this.receivers = queryParams.receivers?.split(',') ?? [];
        this.date = queryParams.date?.split(',').filter(date => !!date).map(date => new Date(date)) ?? null;
        this.subject = queryParams.subject;
    }

    public getCriterias() {
        const criterias: string[] = [];
        if (this.receivers?.length > 0) {
            criterias.push(`RECEIVER_USERNAME IN ("${this.receivers.join('","')}")`);
        }
        if (!!this.date && !!this.date[0]) {
            let formattedMinDate = this.dateToQueryStringDate(this.date[0]);
            if (formattedMinDate) {
                criterias.push(`CREATED_DATE GE "${formattedMinDate}"`);
            }
        }
        if (!!this.date && !!this.date[1]) {
            let formattedMaxDate = this.dateToQueryStringDate(this.date[1]);
            if (formattedMaxDate) {
                criterias.push(`CREATED_DATE LE "${formattedMaxDate}"`);
            }
        }
        if (!!this.subject?.trim()) {
            criterias.push(`SUBJECT ILIKE "%${this.escapeSingleQuotes(this.subject)}%"`);
        }
        return criterias;
    }

    private dateToQueryStringDate(date: Date): string | null | undefined {
        return this.datePipe?.transform(date, "yyyy-MM-dd");
    }

    protected registerParameters() {
        this.registerParam({
            paramName: 'receivers',
            updateFilterFromParam: (param) => (this.receivers = param),
            clearFilter: () => (this.receivers = []),
        });
        this.registerParam({
            paramName: 'date',
            updateFilterFromParam: (param) => (this.date = param),
            clearFilter: () => (this.date = null),
        });
        this.registerParam({
            paramName: 'subject',
            updateFilterFromParam: (param) => (this.subject = param),
            clearFilter: () => (this.subject = null),
        });
    }

    // Override of BaseEntityFilter
    getItemOrId(item: any, field?: string) {
        field = field || 'id';
        let result = item && item[field] ? item[field] : item;
        return result instanceof Date ? this.dateToString(result) : result;
    }

    // Override of BaseEntityFilter
    protected dateToString(date: Date): string {
        return date.toDateString();
    }
}
