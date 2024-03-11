import { HTTP_INTERCEPTORS, HttpClient } from '@angular/common/http';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { ServiceWorkerModule } from '@angular/service-worker';
import { LanguageService } from '@app/shared/service';
import { MissingTranslationHandler, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { ArteTableService } from 'arte-ng';
import { ArteEventManager, PagingParamsResolver, ScrollService } from 'arte-ng/services';
import { CookieService } from 'ngx-cookie-service';
import { ScriptLoaderModule } from 'ngx-script-loader';

import { NgxWebstorageModule } from 'ngx-webstorage';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../environments/environment';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AVAILABLE_LANGUAGES } from './app.constants';
import { ConfigService } from './config';
import { ConfigModule } from './config/config.module';
import { MissingTranslationHandlerImpl } from './config/missing-translation-handler-impl';
import { CoreModule } from './core/core.module';
import { AuthExpiredInterceptor, AuthInterceptor, ErrorHandlerInterceptor } from './core/interceptor';
import { ErrorComponent, ErrorRoutingModule } from './layouts/error';
import { AboutDialogComponent, DeleteConfirmDialogComponent } from './modules/account';
import { SharedModule } from './shared';
import {
    DeleteFilterDialogComponent
} from '@app/modules/filter/filter-form/delete-filter-dialog/delete-filter-dialog.component';
import { SubNavbarComponent } from '@app/layouts/navbar/sub-navbar.component';
import { SubSectionNavbarComponent } from '@app/layouts/navbar/sub-section-navbar.component';


class CustomTranslateHttpLoader extends TranslateHttpLoader {

    private readonly defaultLang: string;

    constructor(http: HttpClient, defaultLang: string, prefix?: string, suffix?: string) {
        super(http, prefix, suffix);
        this.defaultLang = defaultLang;
    }

    getTranslation(lang: string): Observable<Object> {
        return super.getTranslation(lang).pipe(catchError(() => {
            return super.getTranslation(this.defaultLang);
        }));
    }
}

export function createTranslateLoader(http: HttpClient, configService: ConfigService) {
    return new CustomTranslateHttpLoader(http, configService.getConfig().metadata.defaultLanguage, './i18n/', '.json');
}

export function initTranslations(translateService: TranslateService, cookieService: CookieService, configService: ConfigService) {
    const languageCookie = configService.getConfig().metadata.languageCookie;
    const currentLang: string = cookieService.get(languageCookie) || configService.getConfig().metadata.defaultLanguage;
    translateService.setDefaultLang(configService.getConfig().metadata.defaultLanguage);
    translateService.addLangs(AVAILABLE_LANGUAGES);
    return () => translateService.use(currentLang).toPromise();
}

@NgModule({
    declarations: [AppComponent, SubSectionNavbarComponent, SubNavbarComponent, ErrorComponent, DeleteConfirmDialogComponent, DeleteFilterDialogComponent, AboutDialogComponent],
    entryComponents: [DeleteConfirmDialogComponent, AboutDialogComponent, DeleteFilterDialogComponent],
    imports: [
        BrowserModule,
        ErrorRoutingModule,
        NgxWebstorageModule.forRoot({ prefix: 'ac', separator: '-' }),
        CoreModule,
        ConfigModule,
        SharedModule,
        AppRoutingModule,
        TranslateModule.forRoot({
            missingTranslationHandler: { provide: MissingTranslationHandler, useClass: MissingTranslationHandlerImpl },
            loader: {
                provide: TranslateLoader,
                useFactory: createTranslateLoader,
                deps: [HttpClient, ConfigService]
            }
        }),
        ToastModule,
        ServiceWorkerModule.register('./ngsw-worker.js', { enabled: environment.production }),
        ScriptLoaderModule
    ],
    providers: [
        {
            provide: APP_INITIALIZER,
            useFactory: initTranslations,
            deps: [TranslateService, CookieService, ConfigService],
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthInterceptor,
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthExpiredInterceptor,
            multi: true
        },
        {
            provide: APP_INITIALIZER,
            useFactory: (languageService: LanguageService) => () => languageService.init(),
            deps: [LanguageService],
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: ErrorHandlerInterceptor,
            multi: true
        },
        MessageService,
        ArteTableService,
        ArteEventManager,
        PagingParamsResolver,
        ScrollService
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
