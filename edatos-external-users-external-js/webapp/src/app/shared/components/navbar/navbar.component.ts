import { Component, OnInit, ElementRef } from '@angular/core';
import { reinsertScripts } from "@app/shared/utils/htmlUtils";
import { HtmlService } from "@app/core/service";
import { Router } from "@angular/router";
import { SharedRefreshService } from "@app/shared/service";

@Component({
    selector: 'jhi-navbar',
    templateUrl: './navbar.component.html'
})
export class NavbarComponent implements OnInit {

    public navbar = '';

    constructor(
        private elementRef: ElementRef,
        private htmlService: HtmlService,
        private router: Router,
        private sharedService: SharedRefreshService
    ) { }

    ngOnInit() {
        this.htmlService.getHeader().subscribe(data => {
            this.navbar = data.toString();
            setTimeout(() => {
                reinsertScripts(this.elementRef);
                Edatos.UserManagement.addOnLogoutListener(() => {
                    this.router.navigate(['login']);
                    this.sharedService.triggerRefresh();
                    this.sharedService.triggerRefreshForceValue(false);
                });
            });
        })
    }
}
