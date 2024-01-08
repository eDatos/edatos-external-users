import { Component, OnInit } from '@angular/core';
import { ERROR_ALERT_KEY } from './app.constants';
import 'reflect-metadata';
import { Principal } from '@app/core/service';
import { SharedRefreshService } from '@app/shared/service/shared-refresh.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
    public errorAlertKey = ERROR_ALERT_KEY;
    public isAuthenticated: boolean = false;

    constructor(protected principal: Principal, private sharedService: SharedRefreshService) {
        this.sharedService.refreshAppComponent.subscribe(() => {
            this.checkAuthentication();
        });
    }

    async ngOnInit() {
        await this.checkAuthentication();
    }

    private async checkAuthentication() {
        const account = await this.principal.identity();
        this.isAuthenticated = (this.sharedService.getRefreshForceValue() != null) ? this.sharedService.getRefreshForceValue() : !!account;
    }
}
