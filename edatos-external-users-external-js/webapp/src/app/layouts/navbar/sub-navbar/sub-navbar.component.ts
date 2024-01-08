import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PermissionService } from '@app/core/service';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'app-sub-navbar',
    templateUrl: './sub-navbar.component.html',
    styleUrls: ['sub-navbar.component.scss']
})
export class SubNavbarComponent {

    public navLinks: any[];
    public activeLink: string;

    constructor(private router: Router, private route: ActivatedRoute, private translateService: TranslateService,
                public permissionService: PermissionService) {
        this.navLinks = [
            {
                label: this.translateService.instant('filter.home.title'),
                link: 'filter'
            }, {
                label: this.translateService.instant('favorite.home.title'),
                link: 'favorite'
            }
        ];

        const matchingLink = this.navLinks.find((element) => router.url.includes('/' + element.link));
        this.activeLink = matchingLink ? matchingLink.label : '';
    }

}
