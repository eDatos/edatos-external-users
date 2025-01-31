import { Component, OnInit } from "@angular/core";
import { appendContextualFragment } from "@app/shared/utils/htmlUtils";
import { AuthServerProvider, HtmlService } from "@app/core/service";
import { Router } from "@angular/router";

@Component({
    selector: 'jhi-navbar',
    templateUrl: './navbar.component.html'
})
export class NavbarComponent implements OnInit {

    public navbar = '';

    constructor(
        private htmlService: HtmlService,
        private router: Router,
        private authServerProvider: AuthServerProvider,
    ) {
    }

    ngOnInit() {
        this.htmlService.getHeader().subscribe(data => {
            this.navbar = data.toString();
            appendContextualFragment(this.navbar, "header");
            Edatos.UserManagement.addOnLogoutListener(() => {
                if (this.authServerProvider.isUserAuthenticated()) {
                    this.router.navigate(["login"]);
                }
            });
        });
    }
}
