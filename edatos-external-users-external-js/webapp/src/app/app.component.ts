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
        const isSectionFolder = this.isSectionFolder();
        const isSectionAccount = this.isSectionAccount();


        this.isMyFolderSection = (this.navbarRefreshService.getRefreshForceValue() != null) ? this.navbarRefreshService.getRefreshForceValue() : isSectionFolder;
        this.isMyAccountSection = (this.navbarRefreshService.getRefreshForceValue() != null) ? this.navbarRefreshService.getRefreshForceValue() : isSectionAccount;
        this.navbarRefreshService.setRefreshForceRoute(null);
    }

    private isSectionAccount(): boolean {
        const fromValueRoute = this.navbarRefreshService.getRoute() === 'account-management' || this.navbarRefreshService.getRoute() === 'my-section-account';
        return (this.navbarRefreshService.getRoute() != null) ? fromValueRoute : document.location.href.includes('account-management') || document.location.href.includes('notifications')
            || document.location.href.includes('data-protection-policy') || document.location.href.includes('change-password');
    }

    private isSectionFolder(): boolean {
        const fromValueRoute = this.navbarRefreshService.getRoute() === 'filter' || this.navbarRefreshService.getRoute() === 'my-section-folder';
        return (this.navbarRefreshService.getRoute() != null) ? fromValueRoute : document.location.href.includes('filter') || document.location.href.includes('favorite');
    }

}
