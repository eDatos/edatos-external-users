import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Language, User } from '@app/core/model';
import { ActivatedRoute, Router } from '@angular/router';
import { AccountUserService } from '@app/core/service/user';
import { CaptchaService, CodelistService, LanguageService } from '@app/shared/service';
import { MessageService } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';
import {
    DataProtectionPolicyService
} from '@app/modules/account/data-protection-policy/data-protection-policy.service';
import { ItemCode } from '@app/shared';
import { ConfigService } from '@app/config';

type SignUp = Omit<User, 'id' | 'roles'>;

const REGISTRATION_TOKEN_QUERY_PARAM_ID = 'registration-token';

@Component({
    selector: 'app-signup',
    templateUrl: './signup.component.html'
})
export class SignupFormComponent implements OnInit {
    public internationalLanguages: Language[] = [];
    public selectedLanguage: Language;

    public user: User = new User();
    public isSaving: Boolean = false;
    public validUser = false;
    private paramLogin: string;

    public countries: ItemCode[] = [];
    public genders: ItemCode[] = [];
    public nationality: ItemCode;
    public gender: ItemCode;

    public confirmPassword: string;
    public isMigratedUser = false;

    public isCaptchaProviderRecaptcha: boolean = false;
    public simplifiedSignUp: boolean = false;
    private captchaContainerEl: ElementRef;

    private userRegistrationToken?: string;

    private countries$ = this.codeListService.findAllCountries();
    private genders$ = this.codeListService.findAllGenders();


    @ViewChild('captchaContainer')
    set captchaContainer(captchaContainerEl: ElementRef) {
        if (captchaContainerEl && !this.captchaContainerEl) {
            this.captchaContainerEl = captchaContainerEl;
            this.captchaService.buildCaptcha(this.captchaContainerEl);

            const captchaElement = this.captchaContainerEl.nativeElement.querySelector("#captcha-container");
            this.isCaptchaProviderRecaptcha = captchaElement.classList.contains("captcha-recaptcha");
        }
    }

    constructor(
        private accountUserService: AccountUserService,
        private router: Router,
        private captchaService: CaptchaService,
        private messageService: MessageService,
        private translateService: TranslateService,
        private dataProtectionPolicyService: DataProtectionPolicyService,
        private codeListService: CodelistService,
        private activatedRoute: ActivatedRoute,
        private languageService: LanguageService,
        private configService: ConfigService,) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.simplifiedSignUp = this.configService.getConfig().metadata.simplifiedSignUp;
        this.getNationalities();
        this.getGenders();
        this.getInternationalLanguages();

        this.userRegistrationToken = this.activatedRoute.snapshot.queryParams[REGISTRATION_TOKEN_QUERY_PARAM_ID];
        if (this.userRegistrationToken) {
            this.isMigratedUser = true;
            this.loadUserData(this.userRegistrationToken);
        }
    }

    clear() {
        const returnPath = ['signup'];
        if (this.paramLogin) {
            returnPath.push(this.paramLogin);
        }
        this.router.navigate(returnPath);
    }

    save() {
        this.acceptDataProteccionPolicy().then((id) => {
            this.user.dataProtectionPolicyId = id;
            this.isSaving = true;
            if (this.passwordDoNotMatch()) {
                this.validUser = false;
            } else {
                if (!this.isMigratedUser) {
                    this.user.gender = this.gender ? this.gender.id : null;
                    this.user.nationality = this.nationality ? this.nationality.id : null;
                    this.user.language = this.selectedLanguage.locale;
                }
                this.accountUserService
                    .create(this.captchaContainerEl, this.user, this.userRegistrationToken)
                    .then((response) => this.onSaveSuccess(response))
                    .finally(() => (this.isSaving = false));
            }
        });
    }

    private passwordDoNotMatch(): boolean {
        return this.user.password !== this.confirmPassword;
    }

    navigateToLogin() {
        this.router.navigate(['login']);
    }

    acceptDataProteccionPolicy() {
        return new Promise<any>((id, reject) => {
            this.dataProtectionPolicyService.getDataProtectionPolicy().subscribe((dataProtectionPolicy) => {
                if (dataProtectionPolicy.id != null) {
                    id(dataProtectionPolicy.id);
                } else {
                    reject('dataProtectionPolicy cannot be get');
                }
            });
        });
    }

    private getNationalities() {
        this.countries$.subscribe((response) => {
            this.countries = response;
        });
    }

    private getGenders() {
        this.genders$.subscribe((response) => {
            this.genders = response;
        });
    }

    private getInternationalLanguages() {
        this.languageService.getInternationalLanguages().subscribe((response) => {
            this.internationalLanguages = response.map(language => {
                return new Language(language, this.translateService.instant('localeToLanguage.' + language));
            });
            this.internationalLanguages.sort((a, b) => a.label.localeCompare(b.label));
        });
    }

    private loadUserData(userRegistrationToken: string) {
        this.accountUserService.getByRegistrationToken(userRegistrationToken).subscribe(user => {
            if (user == null) {
                this.router.navigate(['signup'], { replaceUrl: true });
                this.isMigratedUser = false;
            } else {
                this.user = user;
            }
        });
    }

    private onSaveSuccess(result) {
        if (this.isMigratedUser) {
            this.messageService.add({
                key: 'customAlertKey',
                severity: 'success',
                summary: this.translateService.instant('signup.messages.onSuccessSumary'),
                detail: this.translateService.instant('signup.messages.onSuccess'),
                life: 5000
            });
        } else {
            this.messageService.add({
                key: 'customAlertKey',
                severity: 'success',
                summary: this.translateService.instant('signup.messages.onSuccessSumary'),
                detail: this.translateService.instant('signup.messages.onRequest'),
                life: 5000
            });
        }
        this.isSaving = false;

        this.navigateToLogin();
    }
}
