import { EventEmitter, Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class SharedRefreshService {
    public refreshAppComponent: EventEmitter<void> = new EventEmitter<void>();
    public refreshForceValue: boolean = null;

    public triggerRefresh() {
        this.refreshAppComponent.emit();
    }

    public triggerRefreshForceValue(refreshForceValue: boolean) {
        this.refreshForceValue = refreshForceValue;
    }

    public getRefreshForceValue(): boolean {
        return this.refreshForceValue;
    }
}
