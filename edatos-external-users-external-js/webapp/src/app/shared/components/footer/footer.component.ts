import { Component, OnInit } from '@angular/core';
import { appendContextualFragment } from "@app/shared/utils/htmlUtils";
import { HtmlService } from "@app/core/service";

@Component({
    selector: 'jhi-footer',
    templateUrl: './footer.component.html'
})
export class FooterComponent implements OnInit {

    public footer = '';

    constructor(private htmlService: HtmlService) {
    }

    ngOnInit() {
        this.htmlService.getFooter().subscribe(data => {
            this.footer = data.toString();
            appendContextualFragment(this.footer, "footer");
        });
    }
}
