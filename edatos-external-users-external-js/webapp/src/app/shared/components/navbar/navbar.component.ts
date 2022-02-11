import { Component, OnInit, ElementRef } from '@angular/core';
import { reinsertScripts } from "@app/shared/utils/htmlUtils";
import { HtmlService } from "@app/core/service";

@Component({
    selector: 'jhi-navbar',
    templateUrl: './navbar.component.html'
})
export class NavbarComponent implements OnInit {

    public navbar = '';

    constructor(
        private elementRef: ElementRef,
        private htmlService: HtmlService
    ) { }

    ngOnInit() {
        this.htmlService.getHeader().subscribe(data => {
            this.navbar = data.toString();
            setTimeout(() => {
                reinsertScripts(this.elementRef);
            });
        })
    }
}
