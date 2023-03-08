import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ExternalUser, Language, Role } from '@app/core/model';
import { PermissionService } from '@app/core/service/auth';
import { CodelistService, ExternalUserService } from '@app/core/service/user';
import { StructuralResourcesTreeComponent } from '@app/shared/components/structural-resources-tree';
import { Favorite, FavoriteResource } from '@app/shared/model';
import { FavoriteService } from '@app/shared/service';
import { ArteEventManager, GenericModalService } from 'arte-ng/services';
import { Subscription } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { ExternalUserDeleteDialogComponent } from '../external-user-delete-dialog.component';

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
    toDate: Date;
    today: Date;

    public rolesEnum = Role;
    public languageEnum = Language;
    public nationality: { code: string; name: string };
    public gender: { code: string; name: string };
    public countries: any[] = [];
    public genders: any[] = [];
    public favorites: Favorite[];
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
        private router: Router,
        private codeListService: CodelistService
    ) {}

    public ngOnInit() {
        this.getToday();
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
                this.getNationalities(this.externalUser.nationality);
                this.getGenders(this.externalUser.gender);
            });
        } else {
            this.externalUser = new ExternalUser();
            this.getNationalities();
            this.getGenders();
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
        this.externalUser.nationality = this.nationality.code.toString();
        this.externalUser.gender = this.gender.code.toString();
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

    public delete() {
        const modalRef = this.genericModalService.open(ExternalUserDeleteDialogComponent as Component, { user: this.externalUser }, { container: '.app' });
        modalRef.result.subscribe((isDelete) => this.onDeleteSuccess(isDelete));
    }

    private onDeleteSuccess(isDelete: boolean) {
        if (isDelete) {
            this.router.navigate(['/external-users']);
        }
    }

    public restore() {
        this.genericModalService.open(ExternalUserDeleteDialogComponent as Component, { user: this.externalUser }, { container: '.app' });
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

    public getToday() {
        const today: Date = new Date();
        const date = new Date(today.getFullYear(), today.getMonth(), today.getDate());
        this.toDate = date;
        this.today = date;
    }

    private updateFavorites() {
        this.favoriteService.findByUserId(this.userId).subscribe((favorites) => {
            this.favorites = favorites;
            this.favoriteTree?.updateSelection();
        });
    }

    private onSaveSuccess(result) {
        this.eventManager.broadcast({ name: 'userListModification', content: 'OK' });
        this.externalUser = result;
        this.isSaving = false;
        this.disabledEdit();
        this.router.navigate(['/external-users', this.externalUser.id]);
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private getNationalities(code?: any) {
        this.codeListService.findAllCountries().subscribe((response) => {
            for (let i = 0; i < response.code.length; i++) {
                let nationalities = { code: response.code[i].id, name: this.codeListService.getValue(response.code[i].name.text) };
                this.countries.push(nationalities);
            }
        });

        if (code) {
            this.codeListService.findNationality(code).subscribe((response) => {
                this.nationality = { code: response.code[0].id, name: this.codeListService.getValue(response.code[0].name.text) };
            });
        }
    }

    private getGenders(code?: any) {
        this.codeListService.findAllGenders().subscribe((response) => {
            for (let i = 0; i < response.code.length; i++) {
                let nationalities = { code: response.code[i].id, name: this.codeListService.getValue(response.code[i].name.text) };
                this.genders.push(nationalities);
            }
        });

        if (code) {
            this.codeListService.findGender(code).subscribe((response) => {
                this.gender = { code: response.code[0].id, name: this.codeListService.getValue(response.code[0].name.text) };
            });
        }
    }
}
