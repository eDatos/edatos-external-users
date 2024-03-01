import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Language, User } from '@app/core/model';
import { AccountUserService } from '@app/core/service/user';
import { TranslateService } from '@ngx-translate/core';
import { MessageService } from 'primeng/api';
import { GenericModalService } from 'arte-ng/services';
import { LoginService } from '@app/core/service';

import { CodelistService, LanguageService } from '@app/shared/service';
import { ItemCode } from '@app/shared';

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

    public internationalLanguages: Language[] = [];
    public selectedLanguage: Language;

    public countries: ItemCode[] = [];
    public genders: ItemCode[] = [];

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
        private languageService: LanguageService
    ) {
    }

    ngOnInit() {
        this.account = this.route.snapshot.data.user;
        this.mapperDataAccount(this.account);
        this.getNationalities(this.account.nationality);
        this.getGenders(this.account.gender);
        this.getInternationalLanguages(this.account.language);
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
