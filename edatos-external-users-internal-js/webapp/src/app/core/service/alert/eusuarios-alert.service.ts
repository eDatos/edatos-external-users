import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { MessageService } from 'primeng/api';

const ERROR_ALERT_KEY = 'alert-errors';
@Injectable()
export class EUsuariosAlertService {
    public DEFAULT_MESSAGE = '';

    constructor(private translateService: TranslateService, private messageService: MessageService) {
        this.DEFAULT_MESSAGE = this.translateService.instant('error.default.message');
    }

    public error(error: any): void {
        const message = this.parseError(error);
        this.addError(message);
    }

    public addError(message: string): void {
        this.messageService.add({
            key: ERROR_ALERT_KEY,
            severity: 'error',
            summary: this.translateService.instant('error.default.title'),
            detail: this.translateService.instant(message),
            sticky: false,
            closable: true,
        });
    }

    public handleResponseError(httpResponse) {
        switch (httpResponse.status) {
            // connection refused, server not reachable
            case 0:
                this.addErrorAlert('error.server.not.reachable');
                break;
            case 409:
            case 400:
            case 404:
                const arr = httpResponse.headers.keys();

                const headers = [] as any[];
                for (let i = 0; i < arr.length; i++) {
                    if (arr[i].endsWith('alert-error') || arr[i].endsWith('alert-params')) {
                        headers.push(arr[i]);
                    }
                }
                headers.sort();
                let errorHeader = null;
                let entityKey = null;
                if (headers.length > 1) {
                    errorHeader = httpResponse.headers.get(headers[0]);
                    entityKey = httpResponse.headers.get(headers[1]);
                }
                if (errorHeader) {
                    const errorMessage = this.translateService.instant(errorHeader);
                    this.addErrorAlert(errorMessage, errorHeader);
                } else if (httpResponse.message !== '' && httpResponse.error && httpResponse.error.fieldErrors) {
                    this.addErrorAlert(httpResponse.error.message);
                    const fieldErrors = httpResponse.error.fieldErrors;
                    for (let i = 0; i < fieldErrors.length; i++) {
                        this.addErrorAlert('error.' + fieldErrors[i].message);
                    }
                } else if (httpResponse.message !== '' && httpResponse.error && httpResponse.error.message) {
                    // CustomParameterizedException
                    this.error(httpResponse.error);
                } else if (httpResponse.message) {
                    this.addErrorAlert(httpResponse.message);
                } else {
                    this.addErrorAlert('error.url.not.found');
                }
                break;
            default:
                if (httpResponse.message !== '' && httpResponse.error && httpResponse.error.message) {
                    this.addErrorAlert(httpResponse.error.message);
                } else {
                    this.addErrorAlert(httpResponse.message);
                }
        }
    }

    private addErrorAlert(message, key?) {
        if (!!message) {
            this.addError(message);
        } else if (key && key !== null) {
            this.addError(key);
        }
    }

    private parseError(error: any): string {
        if (!!error.message) {
            return error.message;
        } else if (!this.hasErrorCode(error)) {
            return this.DEFAULT_MESSAGE;
        } else if (!!error.errorItems && error.errorItems.length > 0) {
            return this.parseErrorList(error);
        } else if (error.params && error.params.length > 0) {
            return this.translateService.instant(error.code, error.params);
        } else {
            return this.translateService.instant(error.code);
        }
    }

    private parseErrorList(error: any): string {
        let formattedHtml = '<div class="alerts-list">';
        if (error.params && error.params.length > 0) {
            formattedHtml += `${this.translateService.instant(error.code, error.params)}`;
        } else {
            formattedHtml += `${this.translateService.instant(error.code)}`;
        }
        formattedHtml += `<ul>`;
        error.errorItems.forEach((errorItem) => {
            const translatedMessage = this.translateService.instant(errorItem.code, errorItem.params);
            formattedHtml += `<li>${translatedMessage}</li>`;
        });
        formattedHtml += `</ul>`;
        formattedHtml += `</div>`;
        return formattedHtml;
    }

    private hasErrorCode(error: any): boolean {
        return !!error.code;
    }
}
