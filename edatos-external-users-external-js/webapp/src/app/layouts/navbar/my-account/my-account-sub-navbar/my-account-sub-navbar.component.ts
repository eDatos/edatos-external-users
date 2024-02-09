import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PermissionService, Principal } from '@app/core/service';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'app-my-account-sub-navbar',
    templateUrl: './my-account-sub-navbar.component.html',
    styleUrls: ['my-account-sub-navbar.component.scss']
})
export class MyAccountSubNavbarComponent implements OnInit {

    public navLinks: any[];
    public activeLink: string;

    public isCorrectlyLogged: boolean;

    constructor(private router: Router, private route: ActivatedRoute, private translateService: TranslateService,
                public permissionService: PermissionService, private principal: Principal) {
    }

    ngOnInit() {
        this.principal.isCorrectlyLogged().subscribe((correctyLogged) => {
            this.isCorrectlyLogged = correctyLogged;
        });

        this.navLinks = [
            {
                label: this.translateService.instant('account.title'),
                link: 'account-management'
            }, {
                label: this.translateService.instant('notices.title'),
                link: 'notifications'
            }
        ];

        const matchingLink = this.navLinks.find((element) => this.router.url.includes('/' + element.link));
        this.activeLink = matchingLink ? matchingLink.label : '';

    }
}
