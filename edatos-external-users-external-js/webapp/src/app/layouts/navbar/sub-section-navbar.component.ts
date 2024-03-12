import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Principal } from '@app/core/service';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'app-sub-section-navbar',
    templateUrl: './sub-section-navbar.component.html',
    styleUrls: ['./sub-navbar.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SubSectionNavbarComponent implements OnInit {

    public folderNavLinks: any[];
    public activeFolderLink: string;
    public accountNavLinks: any[];
    public activeAccountLink: string;


    public isCorrectlyLogged: boolean;

    @Input() public folderSection: boolean;
    @Input() public accountSection: boolean;

    constructor(private router: Router, private route: ActivatedRoute, private translateService: TranslateService,
                private principal: Principal) {
    }

    ngOnInit() {
        this.principal.isCorrectlyLogged().subscribe((correctyLogged) => {
            this.isCorrectlyLogged = correctyLogged;
        });

        this.folderNavLinks = [
            {
                label: this.translateService.instant('filter.home.title'),
                link: 'filter'
            }, {
                label: this.translateService.instant('favorite.home.title'),
                link: 'favorite'
            }
        ];
        const matchingFolderLink = this.folderNavLinks.find((element) => this.router.url.includes('/' + element.link));
        this.activeFolderLink = matchingFolderLink ? matchingFolderLink.label : '';


        this.accountNavLinks = [
            {
                label: this.translateService.instant('account.title'),
                link: 'account-management'
            }, {
                label: this.translateService.instant('notices.title'),
                link: 'notifications'
            }
        ];
        const matchingAccountLink = this.accountNavLinks.find((element) => this.router.url.includes('/' + element.link));
        this.activeAccountLink = matchingAccountLink ? matchingAccountLink.label : '';
    }


}
