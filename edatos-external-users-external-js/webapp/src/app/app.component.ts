import { Component, Inject, OnInit, OnChanges } from '@angular/core';
import { ERROR_ALERT_KEY } from './app.constants';
import 'reflect-metadata';
import { Principal } from '@app/core/service';
import { DOCUMENT } from '@angular/common';
import { NavbarRefreshService } from '@app/shared/service/shared/navbar-refresh.service';
import { filter } from 'rxjs/operators';
import { Router, NavigationEnd } from '@angular/router';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html'
})
export class AppComponent implements OnChanges {
    public errorAlertKey = ERROR_ALERT_KEY;
    public isMyFolderSection: boolean = false;
    public isMyAccountSection: boolean = false;
    public showSubnavbar: boolean;

    constructor(protected principal: Principal,
        private navbarRefreshService: NavbarRefreshService,
        private router: Router,
        @Inject(DOCUMENT) document: any
    ) {
        this.navbarRefreshService.refreshNavbarComponent.subscribe(() => {
            this.checkNavbar();
        });
        this.router.events
            .pipe(filter(event => event instanceof NavigationEnd))
            .subscribe((event: NavigationEnd) => {
                if (event.url === '/login') {
                    this.checkNavbar();
                }
            });
    }

    async ngOnChanges() {
        await this.checkNavbar();
    }

    private async checkNavbar() {
        this.isMyFolderSection = this.isSectionFolder();
        this.isMyAccountSection = this.isSectionAccount();
        this.showSubnavbar = this.isMyFolderSection || this.isMyAccountSection;
        this.navbarRefreshService.setRefreshForceRoute(null);
    }

    private isSectionAccount(): boolean {
        const fromValueRouteAccountManagement = this.navbarRefreshService.getRoute()?.includes('account-management');
        const fromValueRouteNotifications = this.navbarRefreshService.getRoute()?.includes('notifications');
        const fromValueRoute = fromValueRouteAccountManagement
            || fromValueRouteNotifications
            || this.navbarRefreshService.getRoute() === ('account-management')
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
