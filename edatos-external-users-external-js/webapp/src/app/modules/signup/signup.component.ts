import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Language, User } from '@app/core/model';
import {ActivatedRoute, Router} from '@angular/router';
import { AccountUserService } from '@app/core/service/user';
import { CaptchaService, CodelistService } from '@app/shared/service';
import { MessageService } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';
import {
    DataProtectionPolicyService
} from '@app/modules/account/data-protection-policy/data-protection-policy.service';
import { ItemCode } from '@app/shared';
import {forkJoin} from "rxjs";

type SignUp = Omit<User, 'id' | 'roles'>;

const REGISTRATION_TOKEN_QUERY_PARAM_ID = 'registration-token';

@Component({
    selector: 'app-signup',
    templateUrl: './signup.component.html',
    styleUrls: ['signup.component.scss']
})
export class SignupFormComponent implements OnInit {
    public user: User = new User();
    public isSaving: Boolean = false;
    public validUser = false;
    private paramLogin: string;
    public languageEnum = Language;

    public countries: ItemCode[] = [];
    public genders: ItemCode[] = [];
    public nationality: ItemCode;
    public gender: ItemCode;

    public confirmPassword: string;
    public isMigratedUser = false;

    private captchaContainerEl: ElementRef;
    private userRegistrationToken?: string;
    private countries$ = this.codeListService.findAllCountries();
    private genders$ = this.codeListService.findAllGenders();

    @ViewChild('captchaContainer')
    set captchaContainer(captchaContainerEl: ElementRef) {
        if (captchaContainerEl && !this.captchaContainerEl) {
            this.captchaContainerEl = captchaContainerEl;
            this.captchaService.buildCaptcha(this.captchaContainerEl);
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
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.getNationalities();
        this.getGenders();

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
                this.user.gender = this.gender ? this.gender.id : null;
                this.user.nationality = this.nationality ? this.nationality.id : null;
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

    private loadUserData(userRegistrationToken: string) {
        this.accountUserService.getByRegistrationToken(userRegistrationToken).subscribe(user => {
            this.user = user;

            forkJoin([this.countries$, this.genders$]).subscribe(([countries, genders]) => {
                this.gender = genders.find(gender => gender.id === this.user.gender);
                this.nationality = countries.find(nationality => nationality.id === this.user.nationality);
            })
        });
    }
}
