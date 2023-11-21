import { DatePipe } from '@angular/common';
import { Injectable } from '@angular/core';
import { BaseEntityFilter, EntityFilter } from 'arte-ng/model';

@Injectable()
export class NoticeFilter extends BaseEntityFilter implements EntityFilter {
    public message: string;
    public login: string;
    public recivedDate: Date;

    constructor(public datePipe?: DatePipe) {
        super(datePipe);
    }

    protected registerParameters() {
        this.registerParam({
            paramName: 'message',
            updateFilterFromParam: (param) => (this.message = param),
            clearFilter: () => (this.message = null)
        });
        this.registerParam({
            paramName: 'login',
            updateFilterFromParam: (param) => (this.login = param),
            clearFilter: () => (this.login = null)
        });
        this.registerParam({
            paramName: 'recivedDate',
            updateFilterFromParam: (param) => (this.recivedDate = param),
            clearFilter: () => (this.recivedDate = null)
        });
    }

    getCriterias() {
        const criterias = [];
        if (this.message) {
            criterias.push(`NAME ILIKE '%${this.escapeSingleQuotes(this.message)}%'`);
        }
        if (this.login) {
            criterias.push(`LOGIN ILIKE '%${this.escapeSingleQuotes(this.login)}%'`);
        }
        if (this.recivedDate) {
            criterias.push(`LAST_ACCESS_DATE = '%${this.escapeSingleQuotes(this.recivedDate.toString())}%'`);
        }
        return criterias;
    }

    fromCamelCaseToSnakeCase(param: string): string {
        return param.replace(/([A-Z])/g, '_$1').toLowerCase();
    }
}
