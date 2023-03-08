import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Language } from '@app/core/model';
import { AccountUserService } from '@app/core/service/user';
import { TranslateService } from '@ngx-translate/core';
import { MessageService } from 'primeng/api';
import { CodelistService } from '@app/shared/service';

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

    public nationality: { code: string; name: string };
    public gender: { code: string; name: string };

    public languageEnum = Language;

    public countries: any[] = [];
    public genders: any[] = [];

    public downloadingPdf = false;

    constructor(
        private userService: AccountUserService,
        private codeListService: CodelistService,
        private route: ActivatedRoute,
        private router: Router,
        private messageService: MessageService,
        private translateService: TranslateService
    ) {}

    ngOnInit() {
        this.getToday();
        this.isSaving = false;
        this.account = this.route.snapshot.data.user;
        this.getNationalities(this.account.nationality);
        this.getGenders(this.account.gender);
    }

    public save() {
        this.isSaving = true;
        this.account.nationality = this.nationality.code.toString();
        this.account.gender = this.gender.code.toString();
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

    private getNationalities(code?: any) {
        this.codeListService.findAllCountries().subscribe((response) => {
            for (let i = 0; i < response.code.length; i++) {
                let nationalities = { code: response.code[i].id, name: this.codeListService.getValue(response.code[i].name.text) };
                this.countries.push(nationalities);
            }
        });

        if (code) {
            this.codeListService.findNationality(code).subscribe((response) => {
                this.nationality = { code: response.code[0].id, name: this.codeListService.getValue(response.code[0].name.text) };
            });
        }
    }

    private getGenders(code?: any) {
        this.codeListService.findAllGenders().subscribe((response) => {
            for (let i = 0; i < response.code.length; i++) {
                let nationalities = { code: response.code[i].id, name: this.codeListService.getValue(response.code[i].name.text) };
                this.genders.push(nationalities);
            }
        });

        if (code) {
            this.codeListService.findGender(code).subscribe((response) => {
                this.gender = { code: response.code[0].id, name: this.codeListService.getValue(response.code[0].name.text) };
            });
        }
    }
}
