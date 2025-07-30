import { Params } from '@angular/router';
import { BaseEntityFilter, EntityFilter } from 'arte-ng/model';
import { ConfigService } from "@app/config";
import * as luxon from "luxon"
const { DateTime } = luxon;

export class NotificationsFilter extends BaseEntityFilter implements EntityFilter {
    public subject: string | null;
    public date: Date[] | null = null; // null because in this version of Primeng an empty array cause an error in the initialization of the p-calendar
    public receivers: string[] = [];

    constructor(private configService: ConfigService) {
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
            criterias.push(`CREATED_DATE GE "${DateTime
                .fromJSDate(this.date[0], { zone: this.configService.getConfig().timeZone })
                .startOf('day').toUTC().toISO()}"`);
        }
        if (!!this.date && !!this.date[1]) {
            criterias.push(`CREATED_DATE LE "${DateTime
                .fromJSDate(this.date[1], { zone: this.configService.getConfig().timeZone })
                .endOf('day').toUTC().toISO()}"`);
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
