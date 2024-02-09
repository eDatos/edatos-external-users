import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PermissionService, Principal } from '@app/core/service';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'app-my-folder-sub-navbar',
    templateUrl: './my-folder-sub-navbar.component.html',
    styleUrls: ['my-folder-sub-navbar.component.scss']
})
export class MyFolderSubNavbarComponent implements OnInit {

    public isCorrectlyLogged: boolean;
    public navLinks: any[];
    public activeLink: string;

    constructor(private router: Router, private route: ActivatedRoute, private translateService: TranslateService,
                public permissionService: PermissionService, private principal: Principal) {
    }

    ngOnInit() {
        this.principal.isCorrectlyLogged().subscribe((correctyLogged) => {
            this.isCorrectlyLogged = correctyLogged;
        });

        this.navLinks = [
            {
                label: this.translateService.instant('filter.home.title'),
                link: 'filter'
            }, {
                label: this.translateService.instant('favorite.home.title'),
                link: 'favorite'
            }
        ];

        const matchingLink = this.navLinks.find((element) => this.router.url.includes('/' + element.link));
        this.activeLink = matchingLink ? matchingLink.label : '';

    }
}
