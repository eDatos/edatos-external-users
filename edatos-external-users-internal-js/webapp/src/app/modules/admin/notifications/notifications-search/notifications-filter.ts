import { Params } from '@angular/router';
import { BaseEntityFilter, EntityFilter } from 'arte-ng/model';

export class NotificationsFilter extends BaseEntityFilter implements EntityFilter {
    public subject: string | null;
    public minDate: string | null;
    public maxDate: string | null;
    public receivers: string[] = [];

    constructor() {
        super();
    }

    public processUrlParams(queryParams: Params) {
        this.receivers = queryParams.receivers?.split(',') ?? [];
        this.minDate = queryParams.minDate;
        this.maxDate = queryParams.maxDate;
        this.subject = queryParams.subject;
    }

    public getCriterias() {
        const criterias: string[] = [];
        if (this.receivers?.length > 0) {
            criterias.push(`RECEIVER_USERNAME IN ("${this.receivers.join('","')}")`);
        }
        if (!!this.minDate?.trim()) {
            let formattedMinDate = this.displayStringDateToQueryStringDate(this.minDate);
            if (formattedMinDate) {
                criterias.push(`CREATED_DATE GE "${formattedMinDate}"`);
            }
        }
        if (!!this.maxDate?.trim()) {
            let formattedMaxDate = this.displayStringDateToQueryStringDate(this.maxDate);
            if (formattedMaxDate) {
                criterias.push(`CREATED_DATE LE "${formattedMaxDate}"`);
            }
        }
        if (!!this.subject?.trim()) {
            criterias.push(`SUBJECT ILIKE "%${this.escapeSingleQuotes(this.subject)}%"`);
        }
        return criterias;
    }

    // From dd/MM/yyyy to yyyy-MM-dd
    private displayStringDateToQueryStringDate(date: string): string | undefined {
        const DATE_REGEX = /^(\d{2})\/(\d{2})\/(\d{4})$/;
        let dateGroups = DATE_REGEX.exec(date);
        if (dateGroups) {
            return `${dateGroups[3]}-${dateGroups[2]}-${dateGroups[1]}`;
        }
    }

    protected registerParameters() {
        this.registerParam({
            paramName: 'receivers',
            updateFilterFromParam: (param) => (this.receivers = param),
            clearFilter: () => (this.receivers = []),
        });
        this.registerParam({
            paramName: 'minDate',
            updateFilterFromParam: (param) => (this.minDate = param),
            clearFilter: () => (this.minDate = null),
        });
        this.registerParam({
            paramName: 'maxDate',
            updateFilterFromParam: (param) => (this.maxDate = param),
            clearFilter: () => (this.maxDate = null),
        });
        this.registerParam({
            paramName: 'subject',
            updateFilterFromParam: (param) => (this.subject = param),
            clearFilter: () => (this.subject = null),
        });
    }
}
