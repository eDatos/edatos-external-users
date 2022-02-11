import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ConfigService } from "@app/config";

@Injectable()
export class HtmlService {

    constructor(private http: HttpClient, private configService: ConfigService) {}

    getFooter(): Observable<String> {
        return this.http.get(this.configService.getConfig().template.footerUrl, {responseType: 'text', withCredentials: true});
    }

    getHeader(): Observable<String> {
        return this.http.get(this.configService.getConfig().template.headerUrl, {responseType: 'text', withCredentials: true});
    }
}
