import { Component, OnInit, ElementRef } from '@angular/core';
import { ConfigService } from "@app/config";
import { HttpClient } from "@angular/common/http";

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
                this.reinsertScripts();
            });
        })
    }

    private reinsertScripts() {
        const scriptList = this.elementRef.nativeElement.getElementsByTagName('script');
        for (const script of scriptList) {
            const scriptCopy = document.createElement('script');
            if (script.innerHTML) {
                scriptCopy.innerHTML = script.innerHTML;
            } else if (script.src) {
                scriptCopy.src = script.src;
            }
            scriptCopy.async = false;
            script.parentNode.replaceChild(scriptCopy, script);
        }
    }
}
