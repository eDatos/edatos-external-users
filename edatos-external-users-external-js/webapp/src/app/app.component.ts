import { Component, Inject, OnInit } from '@angular/core';
import { ERROR_ALERT_KEY } from './app.constants';
import 'reflect-metadata';
import { Principal } from '@app/core/service';
import { DOCUMENT } from '@angular/common';
import { SharedRefreshService } from '@app/shared/service';
import { NavbarRefreshService } from '@app/shared/service/shared/navbar-refresh.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
    public errorAlertKey = ERROR_ALERT_KEY;
    public isAuthenticated: boolean = false;
    public isMyFolderSection: boolean = false;
    public isMyAccountSection: boolean = false;


    constructor(protected principal: Principal, private sharedService: SharedRefreshService,
                private navbarRefreshService: NavbarRefreshService, @Inject(DOCUMENT) document: any) {
        this.sharedService.refreshAppComponent.subscribe(() => {
            this.checkAuthentication();
        });
        this.navbarRefreshService.refreshNavbarComponent.subscribe(() => {
            this.checkNavbar();
        });
    }

    async ngOnInit() {
        await this.checkAuthentication();
        await this.checkNavbar();
    }

    private async checkAuthentication() {
        const account = await this.principal.identity();
        this.isAuthenticated = (this.sharedService.getRefreshForceValue() != null) ? this.sharedService.getRefreshForceValue() : !!account;
    }

    private async checkNavbar() {
        this.isMyFolderSection = this.isSectionFolder();
        this.isMyAccountSection = this.isSectionAccount();
        this.navbarRefreshService.setRefreshForceRoute(null);
    }

    private isSectionAccount(): boolean {
        const fromValueRouteAccountManagement = this.navbarRefreshService.getRoute()?.includes('account-management');
        const fromValueRouteNotifications = this.navbarRefreshService.getRoute()?.includes('notifications');
        const fromValueRoute = fromValueRouteAccountManagement
            || fromValueRouteNotifications
            || this.navbarRefreshService.getRoute() === ('notifications')
            || this.navbarRefreshService.getRoute() === ('notifications')
            || this.navbarRefreshService.getRoute() === 'data-protection-policy'
            || this.navbarRefreshService.getRoute() === 'change-password';
        return fromValueRoute;
    }

    private isSectionFolder(): boolean {
        const fromValueRouteFilter = this.navbarRefreshService.getRoute()?.includes('filter');
        return fromValueRouteFilter
            || this.navbarRefreshService.getRoute() === 'filter'
            || this.navbarRefreshService.getRoute() === 'favorite';
    }

}
