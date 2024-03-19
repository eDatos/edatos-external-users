import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Language, User } from '@app/core/model';
import { AccountUserService } from '@app/core/service/user';
import { TranslateService } from '@ngx-translate/core';
import { MessageService } from 'primeng/api';
import { CodelistService, LanguageService } from '@app/shared/service';
import { ItemCode } from '@app/shared';
import { EmailModificationConfirmDialogComponent } from "@app/modules/account/management/email-modification-confirm-dialog/email-modification-confirm-dialog.component";
import { GenericModalService } from "arte-ng/services";

@Component({
    selector: 'ac-account-management',
    templateUrl: './account-management-edit.component.html'
})
export class AccountManagementEditComponent implements OnInit {
    error: string;
    success: string;
    public isSaving: Boolean;
    public account: User;

    public nationality: ItemCode;
    public gender: ItemCode;

    public internationalLanguages: Language[] = [];
    public selectedLanguage: Language;

    public countries: ItemCode[] = [];
    public genders: ItemCode[] = [];

    private email: string;

    constructor(
        private userService: AccountUserService,
        private codeListService: CodelistService,
        private route: ActivatedRoute,
        private router: Router,
        private messageService: MessageService,
        private translateService: TranslateService,
        private languageService: LanguageService,
        private genericModalService: GenericModalService
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.account = this.route.snapshot.data.user;
        this.email = this.account.email;
        this.getNationalities(this.account.nationality);
        this.getGenders(this.account.gender);
        this.getInternationalLanguages(this.account.language);
    }

    public save() {
        this.isSaving = true;
        this.account.nationality = this.nationality ? this.nationality.id : null;
        this.account.gender = this.gender ? this.gender.id : null;
        let newEmail: string;
        if (this.email !== this.account.email) {
            newEmail = this.account.email;
            this.account.email = this.email;
        }
        this.userService.update(this.account).subscribe(
            () => {
                this.onSuccess();
                if (!newEmail) {
                    this.router.navigate(['account-management']);
                } else {
                    const modalRef = this.genericModalService.open(EmailModificationConfirmDialogComponent as Component, {
                        account: this.account,
                        newEmail: newEmail
                    }, {
                        container: '.app'
                    });
                    modalRef.result.subscribe(() => this.router.navigate(['account-management']));
                }
            },
            () => {
                this.success = null;
                this.error = 'ERROR';
                this.isSaving = false;
                this.account.email = newEmail;
            }
        );
    }

    public cancel() {
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
            life: 5000
        });
        this.error = null;
        this.success = 'OK';
        this.isSaving = false;
    }

    private getNationalities(code?: any) {
        this.codeListService.findAllCountries().subscribe((response) => {
            this.countries = response;
        });

        if (code) {
            this.codeListService.findNationality(code).subscribe((response) => {
                this.nationality = response;
            });
        }
    }

    private getGenders(code?: any) {
        this.codeListService.findAllGenders().subscribe((response) => {
            this.genders = response;
        });

        if (code) {
            this.codeListService.findGender(code).subscribe((response) => {
                this.gender = response;
            });
        }
    }

    private getInternationalLanguages(language?: string) {
        if (language) {
            this.selectedLanguage = new Language(language, this.translateService.instant('localeToLanguage.' + language));
        }

        this.languageService.getInternationalLanguages().subscribe((response) => {
            this.internationalLanguages = response.map((lang) => {
                return new Language(lang, this.translateService.instant('localeToLanguage.' + lang));
            });
            this.internationalLanguages.sort((a, b) => a.label.localeCompare(b.label));
        });
    }
}
