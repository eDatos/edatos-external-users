import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ConfigService } from "@app/config";

@Injectable()
export class HtmlService {

    constructor(private http: HttpClient, private configService: ConfigService) {}

    getFooter(): Observable<String> {
        return this.http.get(this.configService.getConfig().template.footerUrl, {responseType: 'text', withCredentials: true});
    }

    getHeader(): Observable<String> {
        return this.http.get(this.configService.getConfig().template.headerUrl.concat('&apiKey=' + this.configService.getConfig().metadata.apiKey), {responseType: 'text', withCredentials: true});
    }
}
