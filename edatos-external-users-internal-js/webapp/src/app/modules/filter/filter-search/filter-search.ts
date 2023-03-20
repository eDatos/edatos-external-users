import { DatePipe } from '@angular/common';
import { Injectable } from '@angular/core';
import { Params } from '@angular/router';
import { BaseEntityFilter, EntityFilter } from 'arte-ng/model';
import * as _ from 'lodash';

@Injectable()
export class FilterFilter extends BaseEntityFilter implements EntityFilter {
    public name: string | null;
    public user: string | null;
    public userId: number | null;
    public lastAccessDate: Date | null;

    constructor(public datePipe?: DatePipe) {
        super(datePipe);
    }

    public processUrlParams(queryParams: Params) {
        this.name = queryParams.name;
        this.user = queryParams.user;
        this.userId = queryParams.userId;
        this.lastAccessDate = queryParams.lastAccessDate;
    }

    public getCriterias() {
        const criterias: string[] = [];
        if (this.name) {
            criterias.push(`NAME ILIKE '%${this.escapeSingleQuotes(this.name)}%'`);
        }
        if (this.user) {
            criterias.push(`USER ILIKE '%${this.escapeSingleQuotes(this.user)}%'`);
        }
        if (this.userId) {
            criterias.push(`USER_ID EQ ${this.userId}`);
        }
        if (this.lastAccessDate) {
            criterias.push(`LAST_ACCESS_DATE EQ '%${this.escapeSingleQuotes(this.lastAccessDate.toString())}%'`);
        }
        return criterias;
    }

    protected registerParameters() {
        this.registerParam({
            paramName: 'name',
            updateFilterFromParam: (param) => (this.name = param),
            clearFilter: () => (this.name = null),
        });
        this.registerParam({
            paramName: 'user',
            updateFilterFromParam: (param) => (this.user = param),
            clearFilter: () => (this.user = null),
        });
        this.registerParam({
            paramName: 'userId',
            updateFilterFromParam: (param) => (this.userId = param),
            clearFilter: () => (this.userId = null),
        });
        this.registerParam({
            paramName: 'lastAccessDate',
            updateFilterFromParam: (param) => (this.lastAccessDate = param),
            clearFilter: () => (this.lastAccessDate = null),
        });
    }

    public fromCamelCaseToSnakeCase(param: string): string {
        return param.replace(/([A-Z])/g, '_$1').toLowerCase();
    }
}
