import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AccountUserService, LoginService, Principal, ProfileService } from '@app/core/service';
import { GenericModalService } from 'arte-ng/services';
import { MessageService } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';
import { DeleteConfirmDialogComponent } from '@app/modules/account';
import { User } from '@app/core/model';

@Component({
    selector: 'app-navbar',
    templateUrl: './sub-navbar.component.html',
    styleUrls: ['./navbar.component.scss']
})
export class SubNavbarComponent implements OnInit {
    inProduction: boolean;
    public isCorrectlyLogged: boolean;
    public account: User;

    public downloadingPdf = false;

    @Input() public folderSection: boolean;
    @Input() public accountSection: boolean;

    public folderNavLinks: any[];
    public activeFolderLink: string;
    public accountNavLinks: any[];
    public activeAccountLink: string;

    constructor(
        private loginService: LoginService,
        private principal: Principal,
        private profileService: ProfileService,
        private genericModalService: GenericModalService,
        private userService: AccountUserService,
        private router: Router,
        private route: ActivatedRoute,
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

        this.folderNavLinks = [
            {
                label: this.translateService.instant('filter.home.title'),
                link: 'filter'
            }, {
                label: this.translateService.instant('favorite.home.title'),
                link: 'favorite'
            }
        ];
        const matchingFolderLink = this.folderNavLinks.find((element) => this.router.url.includes('/' + element.link));
        this.activeFolderLink = matchingFolderLink ? matchingFolderLink.label : '';


        this.accountNavLinks = [
            {
                label: this.translateService.instant('account.title'),
                link: 'account-management'
            }, {
                label: this.translateService.instant('notices.title'),
                link: 'notifications'
            }
        ];
        const matchingAccountLink = this.accountNavLinks.find((element) => this.router.url.includes('/' + element.link));
        this.activeAccountLink = matchingAccountLink ? matchingAccountLink.label : '';
    }

    public titleTranslate(): string {
        if (this.folderSection) {
            return this.translateService.instant('global.menu.account.myFolder.title');
        }
        return this.translateService.instant('global.menu.account.myAccount.title');
    }

    public contentTranslate(): string {
        if (this.folderSection) {
            return this.translateService.instant('global.menu.account.myFolder.description');
        }
        return this.translateService.instant('global.menu.account.myAccount.description');
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
