import { Component } from '@angular/core';
import { DEFAULT_LANG } from '@app/app.constants';
import { Principal } from '@app/core/service';
import { TranslateService } from '@ngx-translate/core';
import { DataProtectionPolicyService } from './data-protection-policy.service';

@Component({
    selector: 'app-data-protection-policy',
    templateUrl: './data-protection-policy.component.html',
    styleUrls: ['./data-protection-policy.component.scss'],
})
export class DataProtectionPolicyComponent {
    dataProtectionPolicy: string = '';

    constructor(dataProtectionPolicyService: DataProtectionPolicyService, translateService: TranslateService, principal: Principal) {
        principal.identity();
        dataProtectionPolicyService.getDataProtectionPolicy().subscribe((dataProtectionPolicy) => {
            this.dataProtectionPolicy = dataProtectionPolicy.value.val;
        });
    }
}
