import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Language, User } from '@app/core/model';
import { AccountUserService } from '@app/core/service/user';
import { TranslateService } from '@ngx-translate/core';
import { MessageService } from 'primeng/api';
import { CodelistService } from '@app/shared/service';
import { ItemCode } from '@app/shared';

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

    public languageEnum = Language;

    public countries: ItemCode[] = [];
    public genders: ItemCode[] = [];
    

    constructor(
        private userService: AccountUserService,
        private codeListService: CodelistService,
        private route: ActivatedRoute,
        private router: Router,
        private messageService: MessageService,
        private translateService: TranslateService
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.account = this.route.snapshot.data.user;
        this.getNationalities(this.account.nationality);
        this.getGenders(this.account.gender);
    }

    public save() {
        this.isSaving = true;
        console.log('', this.account);
        this.account.nationality = this.nationality ? this.nationality.id : null;
        this.account.gender = this.gender ? this.gender.id : null;
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
        this.router.navigate(['account-management']);
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
}
