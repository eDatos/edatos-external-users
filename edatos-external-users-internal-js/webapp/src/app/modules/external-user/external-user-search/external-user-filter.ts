import { DatePipe } from '@angular/common';
import { Params } from '@angular/router';
import { Language, Service } from '@app/core/model';
import { BaseEntityFilter, EntityFilter } from 'arte-ng/model';

export class ExternalUserFilter extends BaseEntityFilter implements EntityFilter {
    public fullname: string | null;
    public languages: string[] = [];
    public includeUnregistered: boolean;
    public categories: string[] = [];
    public externalOperations: string[] = [];
    public services: Service[] = [];

    constructor(public datePipe?: DatePipe) {
        super(datePipe);
    }

    public processUrlParams(queryParams: Params) {
        this.includeUnregistered = queryParams.hasOwnProperty('includeUnregistered');
        this.fullname = queryParams.fullname;
        this.languages = queryParams.languages?.split(',') ?? [];
        this.categories = queryParams.categories?.split(',') ?? [];
        this.externalOperations = queryParams.externalOperations?.split(',') ?? [];
        this.services = queryParams.services?.split(' ') ?? [];
    }

    public getCriterias() {
        const criterias: string[] = [];
        if (this.fullname) {
            criterias.push(`FULLNAME ILIKE '%${this.escapeSingleQuotes(this.fullname)}%'`);
        }
        if (this.languages?.length > 0) {
            criterias.push(`LANGUAGE IN (${this.languages.join(',')})`);
        }
        if (!this.includeUnregistered) {
            criterias.push(`ENABLED EQ TRUE`);
        }
        if (this.categories?.length > 0) {
            criterias.push(`FAVORITE_CATEGORIES IN (${this.categories.join(',')})`);
        }
        if (this.externalOperations?.length > 0) {
            criterias.push(`FAVORITE_EXTERNAL_OPERATIONS IN (${this.externalOperations.join(',')})`);
        }
        if (this.services?.length > 0) {
            criterias.push(`SERVICES IN (${this.services.join(',')})`);
        }
        return criterias;
    }

    protected registerParameters() {
        this.registerParam({
            paramName: 'fullname',
            updateFilterFromParam: (param) => (this.fullname = param),
            clearFilter: () => (this.fullname = null),
        });
        this.registerParam({
            paramName: 'languages',
            updateFilterFromParam: (param) => (this.languages = param),
            clearFilter: () => (this.languages = []),
        });
        this.registerParam({
            paramName: 'includeUnregistered',
            updateFilterFromParam: (param) => (this.includeUnregistered = param === 'true'),
            clearFilter: () => (this.includeUnregistered = false),
        });
        this.registerParam({
            paramName: 'categories',
            updateFilterFromParam: (param) => (this.categories = param),
            clearFilter: () => (this.categories = []),
        });
        this.registerParam({
            paramName: 'externalOperations',
            updateFilterFromParam: (param) => (this.externalOperations = param),
            clearFilter: () => (this.externalOperations = []),
        });
        this.registerParam({
            paramName: 'services',
            updateFilterFromParam: (param) => (this.services = param),
            clearFilter: () => (this.services = []),
        });
    }
}
