import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from '@app/core/model';
import { AccountUserService } from '@app/core/service/user';
import { TranslateService } from '@ngx-translate/core';
import { MessageService } from 'primeng/api';
import { CodelistService } from '@app/shared/service';
import { ConfigService } from '@app/config';

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
    public simplifiedSignUp = false;

    constructor(
        private userService: AccountUserService,
        private codeListService: CodelistService,
        private route: ActivatedRoute,
        private router: Router,
        private messageService: MessageService,
        private translateService: TranslateService,
        private configService: ConfigService,
    ) {
    }

    ngOnInit() {
        this.simplifiedSignUp = this.configService.getConfig().metadata.simplifiedSignUp;
        this.account = this.route.snapshot.data.user;
        this.mapperDataAccount(this.account);
        this.getNationalities(this.account.nationality);
        this.getGenders(this.account.gender);
        this.getInternationalLanguages(this.account.language);
    }

    private mapperDataAccount(account: User) {
        this.getNationalities(account.nationality);
        this.getGenders(account.gender);
        this.getInternationalLanguages(account.language);
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

    private getInternationalLanguages(language?: string) {
        if (!language) {
            this.language = '';
        } else {
            this.language = this.translateService.instant('localeToLanguage.' + language);
        }
    }
}
