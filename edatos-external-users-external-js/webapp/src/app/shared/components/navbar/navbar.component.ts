import { Component, OnInit, ElementRef } from '@angular/core';
import { ConfigService } from "@app/config";
import { HttpClient } from "@angular/common/http";
import { reinsertScripts } from "@app/shared/utils/htmlUtils";

@Component({
    selector: 'jhi-navbar',
    templateUrl: './navbar.component.html'
})
export class NavbarComponent implements OnInit {

    public navbar = '';

    constructor(
        private elementRef: ElementRef,
        private configService: ConfigService,
        private http: HttpClient
    ) { }

    ngOnInit() {
        this.http.get(this.configService.getConfig().template.headerUrl, {responseType: 'text', withCredentials: true}).subscribe(data => {
            this.navbar = data.toString();
            setTimeout(() => {
                reinsertScripts(this.elementRef);
            });
        })
    }
}
