import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AccountUserService, LoginService, PermissionService, Principal, ProfileService } from '@app/core/service';
import { AboutDialogComponent } from '@app/modules/account';
import { GenericModalService } from 'arte-ng/services';
import { SharedRefreshService } from '@app/shared/service/shared-refresh.service';

@Component({
    selector: 'app-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['navbar.component.scss']
})
export class NavbarComponent implements OnInit {
    inProduction: boolean;
    public isCorrectlyLogged: boolean;
    public isNavbarCollapsed: boolean;
    public account: any;

    constructor(
        private loginService: LoginService,
        public permissionService: PermissionService,
        private principal: Principal,
        private profileService: ProfileService,
        private genericModalService: GenericModalService,
        private userService: AccountUserService,
        private router: Router,
        private sharedService: SharedRefreshService
    ) {
        this.isNavbarCollapsed = true;
    }

    ngOnInit() {
        this.profileService.getProfileInfo().subscribe((profileInfo) => {
            this.inProduction = profileInfo.inProduction;
        });
        this.principal.isCorrectlyLogged().subscribe((correctyLogged) => {
            this.isCorrectlyLogged = correctyLogged;
        });
    }

    collapseNavbar() {
        this.isNavbarCollapsed = true;
    }

    logout() {
        this.collapseNavbar();
        this.loginService.logout().then(() => this.router.navigate(['login']));
        this.sharedService.triggerRefresh();
        this.sharedService.triggerRefreshForceValue(false);
    }

    toggleNavbar() {
        this.isNavbarCollapsed = !this.isNavbarCollapsed;
    }

    public puedeNavegarAdministracion(): boolean {
        return this.permissionService.puedeNavegarAdministracion();
    }

    public openAboutDialog() {
        this.genericModalService.open(AboutDialogComponent as Component, { user: this.account }, { container: '.app' });
    }
}
