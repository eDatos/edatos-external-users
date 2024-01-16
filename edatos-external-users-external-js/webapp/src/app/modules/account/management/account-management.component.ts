import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from '@app/core/model';
import { AccountUserService } from '@app/core/service/user';
import { TranslateService } from '@ngx-translate/core';
import { MessageService } from 'primeng/api';
import { CodelistService } from '@app/shared/service';
import { DeleteConfirmDialogComponent } from '@app/modules/account';
import { GenericModalService } from 'arte-ng/services';
import { LoginService } from '@app/core/service';
import { SharedRefreshService } from '@app/shared/service/shared-refresh.service';

@Component({
    selector: 'ac-account-management',
    templateUrl: './account-management.component.html',
    styleUrls: ['./account-management.component.scss']
})
export class AccountManagementComponent implements OnInit {
    error: string;
    success: string;
    public account: User;

    public nationality: string;
    public gender: string;
    public language: string;
    public emailNotificationsEnabled: string;

    public downloadingPdf = false;

    constructor(
        private userService: AccountUserService,
        private codeListService: CodelistService,
        private route: ActivatedRoute,
        private router: Router,
        private messageService: MessageService,
        private translateService: TranslateService,
        private genericModalService: GenericModalService,
        private loginService: LoginService,
        private sharedService: SharedRefreshService
    ) {
    }

    ngOnInit() {
        this.account = this.route.snapshot.data.user;
        this.mapperDataAccount(this.account);
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


    private getNationalities(code?: any) {
        if (!code) {
            this.nationality = '';
        } else {
            this.codeListService.findNationality(code).subscribe((response) => {
                this.nationality = response.name;
            });
        }
    }

    private getGenders(code?: any) {
        if (!code) {
            this.gender = '';
        } else {
            this.codeListService.findGender(code).subscribe((response) => {
                this.gender = response.name;
            });
        }
    }

    private mapperDataAccount(account: User) {
        this.getNationalities(this.account.nationality);
        this.getGenders(this.account.gender);
        this.language = this.translateService.instant('language.' + this.account.language);
        this.emailNotificationsEnabled = this.account.emailNotificationsEnabled ? this.translateService.instant('global.yes') : this.translateService.instant('global.no');
    }
}
