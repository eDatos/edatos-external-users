import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Language, Gender } from '@app/core/model';
import { AccountUserService } from '@app/core/service/user';
import { TranslateService } from '@ngx-translate/core';
import { MessageService } from 'primeng/api';
import { Nation } from '@app/core/model/nations.model';

@Component({
    selector: 'ac-account-management',
    templateUrl: './account-management.component.html',
    styleUrls: ['./account-management.component.scss'],
})
export class AccountManagementComponent implements OnInit {
    error: string;
    success: string;
    public isSaving: Boolean;
    public account: any;
    toDate: Date;
    today: Date;
    public nation: string;

    public languageEnum = Language;
    public nationEnum = Nation;
    public genderEnum = Gender;

    public downloadingPdf = false;

    constructor(
        private userService: AccountUserService,
        private route: ActivatedRoute,
        private router: Router,
        private messageService: MessageService,
        private translateService: TranslateService
    ) {}

    ngOnInit() {
        this.getToday();
        this.isSaving = false;
        this.account = this.route.snapshot.data.user;
        this.nation = Object.keys(Nation)[Object.values(Nation).indexOf(this.account.nationality)];
    }

    public save() {
        this.isSaving = true;
        this.account.nationality = this.nation;
        this.userService.update(this.account).subscribe(
            () => {
                this.onSuccess();
            },
            () => {
                this.success = null;
                this.error = 'ERROR';
                this.isSaving = false;
            }
        );
    }

    public clear() {
        const returnPath = ['account-management'];
        this.router.navigate(returnPath);
    }

    public isEditMode(): Boolean {
        if (this.route.snapshot.url.length == 0) {
            return false;
        }
        const lastPath = this.route.snapshot.url[this.route.snapshot.url.length - 1].path;
        return lastPath === 'edit';
    }

    private onSuccess() {
        this.messageService.add({
            key: 'customAlertKey',
            severity: 'success',
            summary: this.translateService.instant('account.messages.onSuccessSumary'),
            detail: this.translateService.instant('account.messages.onSuccess'),
            life: 5000,
        });
        this.error = null;
        this.success = 'OK';
        this.isSaving = false;
        this.router.navigate(['account-management']);
    }

    getToday() {
        const today: Date = new Date();
        const date = new Date(today.getFullYear(), today.getMonth(), today.getDate());
        this.toDate = date;
        this.today = date;
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
                    life: 5000,
                }),
            () => (this.downloadingPdf = false)
        );
    }
}
