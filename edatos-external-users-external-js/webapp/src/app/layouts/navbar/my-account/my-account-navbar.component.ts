import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AccountUserService, LoginService, PermissionService, Principal, ProfileService } from '@app/core/service';
import { AboutDialogComponent, DeleteConfirmDialogComponent } from '@app/modules/account';
import { GenericModalService } from 'arte-ng/services';
import { MessageService } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';
import { SharedRefreshService } from '@app/shared/service';
import { NavbarRefreshService } from '@app/shared/service/shared/navbar-refresh.service';
import { User } from '@app/core/model';

@Component({
    selector: 'app-my-account-navbar',
    templateUrl: './my-account-navbar.component.html',
    styleUrls: ['../navbar.component.scss']
})
export class MyAccountNavbarComponent implements OnInit {
    inProduction: boolean;
    public isCorrectlyLogged: boolean;
    public isNavbarCollapsed: boolean = true;
    public account: User;

    public downloadingPdf = false;

    constructor(
        private loginService: LoginService,
        public permissionService: PermissionService,
        private principal: Principal,
        private profileService: ProfileService,
        private genericModalService: GenericModalService,
        private userService: AccountUserService,
        private router: Router,
        private route: ActivatedRoute,
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

        this.userService.getLogueado().subscribe((user) => {
            this.account = user;
        });
    }

    public navigateTo(route: string) {
        this.isNavbarCollapsed = true;
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

    public openAboutDialog() {
        this.genericModalService.open(AboutDialogComponent as Component, { user: this.account }, { container: '.app' });
        this.toggleNavbar();
    }

    public openDeleteDialog() {
        this.userService
            .getLogueado()
            .toPromise()
            .then((account) => {
                const modalRef = this.genericModalService.open(DeleteConfirmDialogComponent as Component, { user: { ...account } }, { container: '.app' });
                modalRef.result.subscribe((isDelete) => this.onDeleteSuccess(isDelete));
            });
    }

    private onDeleteSuccess(isDelete: boolean) {
        if (isDelete) {
            this.loginService.logout().then(() => this.router.navigate(['login']));
            this.sharedService.triggerRefresh();
            this.sharedService.triggerRefreshForceValue(false);
        }
    }

    public download(event: Event) {
        this.downloadingPdf = true;
        this.userService.download(this.account.id).subscribe(
            (response) => this.userService.saveToFileSystem(response),
            (error) =>
                this.messageService.add({
                    key: 'customAlertKey',
                    severity: 'error',
                    summary: this.translateService.instant('signup.document.onErrorDownload.label'),
                    detail: this.translateService.instant('signup.document.onErrorDownload.detail'),
                    life: 5000
                }),
            () => (this.downloadingPdf = false)
        );
    }
}
