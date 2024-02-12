import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AccountUserService, LoginService, PermissionService, Principal, ProfileService } from '@app/core/service';
import { GenericModalService } from 'arte-ng/services';
import { MessageService } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';
import { SharedRefreshService } from '@app/shared/service';
import { NavbarRefreshService } from '@app/shared/service/shared/navbar-refresh.service';

@Component({
    selector: 'app-my-folder-navbar',
    templateUrl: './my-folder-navbar.component.html',
    styleUrls: ['../navbar.component.scss']
})
export class MyFolderNavbarComponent implements OnInit {
    inProduction: boolean;
    public isCorrectlyLogged: boolean;
    public isNavbarCollapsed: boolean = true;
    public account: any;

    constructor(
        private loginService: LoginService,
        public permissionService: PermissionService,
        private principal: Principal,
        private profileService: ProfileService,
        private genericModalService: GenericModalService,
        private userService: AccountUserService,
        private router: Router,
        private sharedService: SharedRefreshService,
        private navbarRefreshService: NavbarRefreshService,
        private messageService: MessageService,
        private translateService: TranslateService
    ) {
    }

    ngOnInit() {
        this.profileService.getProfileInfo().subscribe((profileInfo) => {
            this.inProduction = profileInfo.inProduction;
        });
        this.principal.isCorrectlyLogged().subscribe((correctyLogged) => {
            this.isCorrectlyLogged = correctyLogged;
        });
    }

    public navigateTo(route: string) {
        this.isNavbarCollapsed = true;
        this.navbarRefreshService.setRefreshForceRoute(route);
        this.navbarRefreshService.triggerRefresh();
        this.router.navigate([route]);
    }

    logout() {
        this.isNavbarCollapsed = true;
        this.loginService.logout().then(() => this.router.navigate(['login']));
        this.sharedService.triggerRefresh();
        this.sharedService.triggerRefreshForceValue(false);
    }

    toggleNavbar() {
        this.isNavbarCollapsed = !this.isNavbarCollapsed;
    }

}
