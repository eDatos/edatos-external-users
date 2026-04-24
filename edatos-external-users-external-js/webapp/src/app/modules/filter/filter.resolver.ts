import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Filter } from '@app/shared/model';
import { FilterService } from '@app/shared/service';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class FilterResolver implements Resolve<Filter> {
    constructor(private filterService: FilterService) {}

    public resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Filter> {
        const filterId = (route.paramMap.get('id') as unknown) as number;
        return this.filterService.get(filterId);
    }
}
