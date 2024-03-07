import { EventEmitter, Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class NavbarRefreshService {
    public refreshNavbarComponent: EventEmitter<void> = new EventEmitter<void>();
    public refreshForceValue: boolean = null;
    private _route: string = null;

    public triggerRefresh() {
        this.refreshNavbarComponent.emit();
    }

    public getRefreshForceValue(): boolean {
        return this.refreshForceValue;
    }

    public setRefreshForceRoute(route: string) {
        this._route = route;
    }

    public getRoute(): string {
        return this._route;
    }
}
