import { Component, OnInit, ElementRef } from '@angular/core';
import { reinsertScripts } from "@app/shared/utils/htmlUtils";
import { AuthServerProvider, HtmlService } from "@app/core/service";
import { Router } from "@angular/router";

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
        private authServerProvider: AuthServerProvider
    ) { }

    ngOnInit() {
        this.htmlService.getHeader().subscribe(data => {
            this.navbar = data.toString();
            setTimeout(() => {
                reinsertScripts(this.elementRef);
                Edatos.UserManagement.addOnLogoutListener(() => {
                    if (this.authServerProvider.isUserAuthenticated()) {
                        this.router.navigate(['login']);
                    }
                });
            });
        })
    }
}
