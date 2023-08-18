import { HTTP_INTERCEPTORS, HttpClient } from '@angular/common/http';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { ServiceWorkerModule } from '@angular/service-worker';
import { LanguageService } from '@app/shared/service';
import { LoadingBarModule } from '@ngx-loading-bar/core';
import { LoadingBarHttpClientModule } from '@ngx-loading-bar/http-client';
import { LoadingBarRouterModule } from '@ngx-loading-bar/router';
import { MissingTranslationHandler, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { ArteTableService } from 'arte-ng';
import { ArteEventManager, PagingParamsResolver, ScrollService } from 'arte-ng/services';
import { CookieService } from 'ngx-cookie';
import { ScriptLoaderModule } from 'ngx-script-loader';

import { NgxWebstorageModule } from 'ngx-webstorage';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { environment } from '../environments/environment';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AVAILABLE_LANGUAGES } from './app.constants';
import { ConfigService } from './config';
import { ConfigModule } from './config/config.module';
import { MissingTranslationHandlerImpl } from './config/missing-translation-handler-impl';
import { CoreModule } from './core/core.module';
import { AuthExpiredInterceptor, AuthInterceptor, ClassToPlainInterceptor, ErrorHandlerInterceptor } from './core/interceptor';
import { EUsuariosAlertService } from './core/service/alert/eusuarios-alert.service';
import { AuthServerProvider } from './core/service/auth';
import { EdatosNavbarComponent } from './layouts/edatos-navbar/edatos-navbar.component';
import { ErrorComponent, ErrorRoutingModule } from './layouts/error';
import { NavbarComponent } from './layouts/navbar';
import { SharedModule } from './shared';

export function init(configService: ConfigService, authServerProvider: AuthServerProvider) {
    return () => {
        const promise: Promise<boolean> = new Promise((resolve, reject) => {
            if (authServerProvider.getToken()) {
                resolve(true);
            } else {
                const config = configService.getConfig();
                window.location.href = config.cas.login + '?service=' + encodeURIComponent(config.cas.service);
            }
        });
        return promise;
    };
}

export function createTranslateLoader(http: HttpClient) {
    return new TranslateHttpLoader(http, './i18n/', '.json');
}

export function initTranslations(translateService: TranslateService, cookieService: CookieService, configService: ConfigService) {
    const currentLang: string = configService.getConfig().metadata.language || configService.getConfig().metadata.defaultLanguage;
    translateService.setDefaultLang(configService.getConfig().metadata.defaultLanguage);
    translateService.addLangs(AVAILABLE_LANGUAGES);
    return () => translateService.use(currentLang).toPromise();
}
@NgModule({
    declarations: [AppComponent, NavbarComponent, ErrorComponent, EdatosNavbarComponent],
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
                deps: [HttpClient],
            },
        }),
        ToastModule,
        ServiceWorkerModule.register('./ngsw-worker.js', { enabled: environment.production }),
        ScriptLoaderModule,
        LoadingBarHttpClientModule,
        LoadingBarRouterModule,
        LoadingBarModule,
    ],
    providers: [
        {
            provide: APP_INITIALIZER,
            useFactory: init,
            deps: [ConfigService, AuthServerProvider],
            multi: true,
        },
        {
            provide: APP_INITIALIZER,
            useFactory: initTranslations,
            deps: [TranslateService, CookieService, ConfigService],
            multi: true,
        },
        {
            provide: APP_INITIALIZER,
            useFactory: (languageService: LanguageService) => () => languageService.init(),
            deps: [LanguageService],
            multi: true,
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthInterceptor,
            multi: true,
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthExpiredInterceptor,
            multi: true,
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: ErrorHandlerInterceptor,
            multi: true,
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: ClassToPlainInterceptor,
            multi: true,
        },
        MessageService,
        ArteTableService,
        ArteEventManager,
        PagingParamsResolver,
        EUsuariosAlertService,
        ScrollService,
    ],
    bootstrap: [AppComponent],
})
export class AppModule {}
