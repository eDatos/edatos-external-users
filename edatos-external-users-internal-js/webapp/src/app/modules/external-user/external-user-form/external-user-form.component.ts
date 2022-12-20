import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ExternalUser, Language, Role} from '@app/core/model';
import {PermissionService} from '@app/core/service/auth';
import {ExternalUserService} from '@app/core/service/user';
import {StructuralResourcesTreeComponent} from '@app/shared/components/structural-resources-tree';
import {Favorite, FavoriteResource} from '@app/shared/model';
import {FavoriteService} from '@app/shared/service';
import {ArteEventManager, GenericModalService} from 'arte-ng/services';
import {Subscription} from 'rxjs';
import {finalize} from 'rxjs/operators';
import {ExternalUserDeleteDialogComponent} from '../external-user-delete-dialog.component';
import {Gender} from '@app/core/model/gender.model';

@Component({
    selector: 'app-external-user-form',
    templateUrl: './external-user-form.component.html',
    styleUrls: ['./external-user-form.component.scss'],
})
export class ExternalUserFormComponent implements OnInit, OnDestroy {
    public externalUser: ExternalUser;
    public isSaving: boolean;
    public userId: number;
    public eventSubscriber: Subscription;

    public rolesEnum = Role;
    public languageEnum = Language;
    public genderEnum = Gender;
    public favorites: Favorite[];
    predefinedCountries: any[] = [
        {
            name: 'Germany',
            alpha2Code: 'DE',
            alpha3Code: 'DEU',
            numericCode: '276',
        },
        {
            name: 'Greece',
            alpha2Code: 'GR',
            alpha3Code: 'GRC',
            numericCode: '300',
        },
        {
            name: 'France',
            alpha2Code: 'FR',
            alpha3Code: 'FRA',
            numericCode: '250',
        },
        {
            name: 'Belgium',
            alpha2Code: 'BE',
            alpha3Code: 'BEL',
            numericCode: '056',
        },
    ];

    public editMode: 'user' | 'favorites' | null = null;

    @ViewChild(StructuralResourcesTreeComponent)
    public favoriteTree: StructuralResourcesTreeComponent;

    private subscription: Subscription;

    constructor(
        private externalUserService: ExternalUserService,
        public permissionService: PermissionService,
        private genericModalService: GenericModalService,
        private favoriteService: FavoriteService,
        private eventManager: ArteEventManager,
        private route: ActivatedRoute,
        private router: Router
    ) {
    }

    public ngOnInit() {
        this.isSaving = false;

        this.subscription = this.route.params.subscribe((params) => {
            this.userId = params['id'];
            this.load(this.userId);
        });

        this.eventSubscriber = this.eventManager.subscribe(ExternalUserDeleteDialogComponent.EVENT_NAME, (response) => {
            this.externalUser = Object.assign(new ExternalUser(), response.content);
        });
    }

    public createFavorite(resource: FavoriteResource): Favorite {
        const favorite = new Favorite();
        favorite.externalUser = this.externalUser;
        favorite.resource = resource;
        return favorite;
    }

    public saveFavorites() {
        if (this.externalUser.id && this.favoriteTree.favorites) {
            this.favoriteService
                .updateFavorites(
                    this.favoriteTree.selectedResources.map((resource) => this.createFavorite(resource.data)),
                    this.externalUser
                )
                .subscribe({
                    next: (favorites) => (this.favorites = favorites),
                    error: () => this.updateFavorites(),
                });
        } else {
            this.router.navigate(['..']);
        }
        this.disabledEdit();
    }

    public load(id: number) {
        if (id) {
            if (this.editMode !== 'user') {
                this.updateFavorites();
            }
            this.externalUserService.get(id).subscribe((user) => {
                this.externalUser = user;
            });
        } else {
            this.externalUser = new ExternalUser();
        }
    }

    public cancel() {
        if (this.userId) {
            this.load(this.userId);
        } else {
            this.router.navigate(['..']);
        }
        this.disabledEdit();
    }

    public save() {
        this.isSaving = true;
        if (this.userId) {
            this.externalUserService.update(this.externalUser).subscribe(
                (response) => this.onSaveSuccess(response),
                () => this.onSaveError()
            );
        } else {
            this.externalUserService.create(this.externalUser).subscribe(
                (response) => this.onSaveSuccess(response),
                () => this.onSaveError()
            );
        }
        this.disabledEdit();
    }

    public deactivate() {
        this.genericModalService.open(ExternalUserDeleteDialogComponent as Component, {user: this.externalUser}, {container: '.app'});
    }

    public restore() {
        this.genericModalService.open(ExternalUserDeleteDialogComponent as Component, {user: this.externalUser}, {container: '.app'});
    }

    public ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public deleteFavorite(resource: FavoriteResource): void {
        const favorite = this.favorites.find((fav) => fav.resource?.id === resource.id && fav.resource?.favoriteType === resource.favoriteType)!;
        this.favoriteService
            .delete(favorite.id)
            .pipe(finalize(() => this.updateFavorites()))
            .subscribe();
    }

    public disabledEdit() {
        this.editMode = null;
    }

    public editUserData() {
        this.editMode = 'user';
    }

    public editFavorites() {
        this.editMode = 'favorites';
        if (!this.userId) {
            this.router.navigate(['..']);
        }
    }

    private updateFavorites() {
        this.favoriteService.findByUserId(this.userId).subscribe((favorites) => {
            this.favorites = favorites;
            this.favoriteTree?.updateSelection();
        });
    }

    private onSaveSuccess(result) {
        this.eventManager.broadcast({name: 'userListModification', content: 'OK'});
        this.externalUser = result;
        this.isSaving = false;
        this.disabledEdit();
        this.router.navigate(['/external-users', this.externalUser.id]);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}
