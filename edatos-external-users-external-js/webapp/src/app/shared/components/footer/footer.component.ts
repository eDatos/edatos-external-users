import { Component, OnInit, ElementRef } from '@angular/core';
import { ConfigService } from "@app/config";
import { HttpClient } from "@angular/common/http";
import { reinsertScripts } from "@app/shared/utils/htmlUtils";

@Component({
    selector: 'jhi-footer',
    templateUrl: './footer.component.html'
})
export class FooterComponent implements OnInit {

    public footer = '';

    constructor(
        private elementRef: ElementRef,
        private configService: ConfigService,
        private http: HttpClient
    ) { }

    ngOnInit() {
        this.http.get(this.configService.getConfig().template.footerUrl, {responseType: 'text', withCredentials: true}).subscribe(data => {
            this.footer = data.toString();
            setTimeout(() => {
                reinsertScripts(this.elementRef);
            });
        })
    }
}
