import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { MessageService } from 'primeng/api';

const ERROR_ALERT_KEY = 'alert-errors';

type MESSAGE_SEVERITY_PROPERTIES = { severity: string, summary?: string };
const MESSAGE_SEVERITY: {[key: string]: MESSAGE_SEVERITY_PROPERTIES} = {
    ERROR: {
        severity: 'error'
    },
    WARN: {
        severity: 'warn'
    }
};
@Injectable()
export class EUsuariosAlertService {
    DEFAULT_MESSAGE = "";

    constructor(private translateService: TranslateService, private messageService: MessageService) {
        this.DEFAULT_MESSAGE = this.translateService.instant('error.default.message')
    }

    public error(error: any): void {
        const message = this.parseError(error);
        this.addError(message);
    }

    private addMessage(message: string, severityProperties: MESSAGE_SEVERITY_PROPERTIES) {
        this.messageService.add({
            key: ERROR_ALERT_KEY,
            sticky: false,
            closable: true,
            summary: this.translateService.instant('error.default.title'),
            detail: this.translateService.instant(message),
            ...severityProperties
        });
    }

    public addError(message: string): void {
        this.addMessage(message, MESSAGE_SEVERITY.ERROR);
    }

    public addWarning(message: string): void {
        this.addMessage(message, MESSAGE_SEVERITY.WARN);
    }

    public handleResponseError(httpResponse) {
        switch (httpResponse.status) {
            // connection refused, server not reachable
            case 0:
                this.addErrorAlert('error.server.not.reachable');
                break;
            case 200:
                const notificationExceptionItems = httpResponse.body.notificationException.exceptionItems;
                if (notificationExceptionItems.length > 0) {
                    this.addWarningAlert(notificationExceptionItems[notificationExceptionItems.length - 1].message, null);
                } else {
                    this.addWarningAlert(httpResponse.body.notificationException.humanReadableMessage, null);
                }
                break;
            case 409:
            case 400:
            case 404:
                const arr = httpResponse.headers.keys();
                const headers = [];
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

    private addWarningAlert(message, key?) {
        if (!!message) {
            this.addWarning(message);
        } else if (key && key !== null) {
            this.addWarning(key);
        }
    }

    private parseError(error: any): string {
        if (!this.hasErrorCode(error)) {
            return error.message ? error.message : this.DEFAULT_MESSAGE;
        }

        if (!!error.errorItems && error.errorItems.length > 0) {
            return this.parseErrorList(error);
        } else if (error.params && error.params.length > 0) {
            return this.translateService.instant(error.code, error.params);
        } else {
            return this.translateService.instant(error.code);
        }
    }

    private parseErrorList(error: any): string {
        let formattedHtml = '';
        if (error.params && error.params.length > 0) {
            formattedHtml += `${this.translateService.instant(error.code, error.params)}`;
        } else {
            formattedHtml += `${this.translateService.instant(error.code)}`;
        }

        error.errorItems.forEach((errorItem) => {
            const translatedMessage = this.translateService.instant(errorItem.code, errorItem.params);
            formattedHtml += `\n• ${translatedMessage}`;
        });
        return formattedHtml;
    }

    private hasErrorCode(error: any): boolean {
        return !!error.code;
    }
}
