import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataProtectionPolicy } from '@app/shared/model';
import { TranslateService } from '@ngx-translate/core';
import { DataProtectionPolicyService } from './data-protection-policy.service';

@Component({
    selector: 'app-data-protection-policy',
    templateUrl: './data-protection-policy.component.html',
    styleUrls: [],
})
export class DataProtectionPolicyComponent {
    public dataProtectionPolicy: DataProtectionPolicy = new DataProtectionPolicy();
    public isEditMode = false;
    public isDataProtectionPolicySaving = false;
    public locales: string[] = [];
    public locale = '';

    constructor(route: ActivatedRoute, private router: Router, private dataProtectionPolicyService: DataProtectionPolicyService, translateService: TranslateService) {
        this.locale = translateService.getDefaultLang();
        if (route.snapshot.url.length !== 0) {
            const lastPath = route.snapshot.url[route.snapshot.url.length - 1].path;
            this.isEditMode = lastPath === 'edit';
        }

        this.dataProtectionPolicyService.getDataProtectionPolicy().subscribe((dataProtectionPolicy) => {
            this.dataProtectionPolicy = dataProtectionPolicy;
            this.locales = this.dataProtectionPolicy.value.texts.map((text) => text.locale).sort((a, b) => (a < b ? -1 : +(a > b)));
        });
    }

    public clear() {
        // const with arrays: https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/const
        const returnPath = ['/admin/data-protection-policy'];
        this.router.navigate(returnPath);
    }

    public save() {
        this.isDataProtectionPolicySaving = true;
        this.dataProtectionPolicyService.updateDataProtectionPolicy(this.dataProtectionPolicy).subscribe(() => {
            this.clear();
            this.isDataProtectionPolicySaving = false;
        }, () => {
            this.isDataProtectionPolicySaving = false;
        });
    }
}
