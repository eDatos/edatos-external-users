import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ExternalUser, Language } from '@app/core/model';
import { PermissionService } from '@app/core/service/auth';
import { CodelistService, ExternalUserService } from '@app/core/service/user';
import { Favorite } from '@app/shared/model';
import { ItemCode } from '@app/shared/model/external-item-code-response.model';
import { FavoriteService } from '@app/shared/service';
import { TranslateService } from '@ngx-translate/core';
import { ArteEventManager, GenericModalService } from 'arte-ng/services';
import { Subscription } from 'rxjs';
import { ExternalUserDeleteDialogComponent } from '../external-user-delete-dialog.component';
import { StructuralResourcesTreeSelectComponent } from "@app/shared/components/structural-resources-tree/structural-resources-tree-select/structural-resources-tree-select.component";
import { ConfigService } from '@app/config';

@Component({
    selector: 'app-external-user-form',
    templateUrl: './external-user-form.component.html',
    styleUrls: ['./external-user-form.component.scss'],
})
export class ExternalUserFormComponent implements OnInit, OnDestroy {
    public externalUser: ExternalUser;
    public userId: number;

    public selectedLanguage: Language;
    public nationality: ItemCode;
    public gender: ItemCode;
    public favorites: Favorite[];
    public simplifiedSignUp = false;

    @ViewChild(StructuralResourcesTreeSelectComponent)
    public favoriteTree: StructuralResourcesTreeSelectComponent;

    private subscription: Subscription;

    constructor(
        private externalUserService: ExternalUserService,
        public permissionService: PermissionService,
        private genericModalService: GenericModalService,
        private favoriteService: FavoriteService,
        private eventManager: ArteEventManager,
        private route: ActivatedRoute,
        private router: Router,
        private codeListService: CodelistService,
        private translateService: TranslateService,
        private configService: ConfigService,
    ) {}

    public ngOnInit() {
        this.simplifiedSignUp = this.configService.getConfig().metadata.simplifiedSignUp;
        this.subscription = this.route.params.subscribe((params) => {
            this.userId = params['id'];
            this.load(this.userId);
        });
    }

    public ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public load(id: number) {
        if (id) {
            this.updateFavorites();
            this.externalUserService.get(id).subscribe((user) => {
                this.externalUser = user;
                this.getNationality(this.externalUser.nationality);
                this.getGender(this.externalUser.gender);
                this.getInternationalLanguage(this.externalUser.language);
            });
        } else {
            this.externalUser = new ExternalUser();
            this.getNationality();
            this.getGender();
            this.getInternationalLanguage();
        }
    }

    public delete() {
        const modalRef = this.genericModalService.open(ExternalUserDeleteDialogComponent as Component, { user: this.externalUser }, { container: '.app' });
        modalRef.result.subscribe((isDelete) => this.onDeleteSuccess(isDelete));
    }

    private onDeleteSuccess(isDelete: boolean) {
        if (isDelete) {
            this.router.navigate(['/external-users']);
        }
    }

    private updateFavorites() {
        this.favoriteService.findByUserId(this.userId).subscribe((favorites) => {
            this.favorites = favorites;
            this.favoriteTree?.updateSelection();
        });
    }

    private getNationality(code?: any) {
        if (code) {
            this.codeListService.findNationality(code).subscribe((response) => {
                this.nationality = response;
            });
        }
    }

    private getGender(code?: any) {
        if (code) {
            this.codeListService.findGender(code).subscribe((response) => {
                this.gender = response;
            });
        }
    }

    private getInternationalLanguage(language?: string) {
        if (language) {
            this.selectedLanguage = new Language(language, this.translateService.instant('enum.localeToLanguage.' + language.toUpperCase()));
        }
    }
}
