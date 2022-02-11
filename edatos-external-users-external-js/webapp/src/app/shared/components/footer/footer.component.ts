import { Component, OnInit, ElementRef } from '@angular/core';
import { reinsertScripts } from "@app/shared/utils/htmlUtils";
import { HtmlService } from "@app/core/service";

@Component({
    selector: 'jhi-footer',
    templateUrl: './footer.component.html'
})
export class FooterComponent implements OnInit {

    public footer = '';

    constructor(
        private elementRef: ElementRef,
        private htmlService: HtmlService
    ) { }

    ngOnInit() {
        this.htmlService.getHeader().subscribe(data => {
            this.footer = data.toString();
            setTimeout(() => {
                reinsertScripts(this.elementRef);
            });
        })
    }
}
