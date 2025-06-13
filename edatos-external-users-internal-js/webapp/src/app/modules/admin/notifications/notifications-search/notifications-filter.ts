import { Params } from '@angular/router';
import { BaseEntityFilter, EntityFilter } from 'arte-ng/model';

export class NotificationsFilter extends BaseEntityFilter implements EntityFilter {
    public subject: string | null;
    public date: Date[] | null = null; // null because in this version of Primeng an empty array cause an error in the initialization of the p-calendar
    public receivers: string[] = [];

    constructor() {
        super();
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
            let startDate = new Date(this.date[0].getTime());
            startDate.setHours(0, 0, 0, 0);
            criterias.push(`CREATED_DATE GE "${startDate.toISOString()}"`);
        }
        if (!!this.date && !!this.date[1]) {
            let endDate = new Date(this.date[1].getTime());
            endDate.setHours(23, 59, 59, 999);
            criterias.push(`CREATED_DATE LE "${endDate.toISOString()}"`);
        }
        if (!!this.subject?.trim()) {
            criterias.push(`SUBJECT ILIKE "${this.escapeSingleQuotes(this.subject)}"`);
        }
        return criterias;
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
