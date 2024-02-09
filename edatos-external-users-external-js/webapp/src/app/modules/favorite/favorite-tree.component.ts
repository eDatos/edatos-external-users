import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { User } from '@app/core/model';
import { CategoryTreeNode } from '@app/shared/components/structural-resources-tree';
import { Favorite } from '@app/shared/model';
import { FavoriteService } from '@app/shared/service/favorite/favorite.service';
import { TranslateService } from '@ngx-translate/core';
import { finalize } from 'rxjs/operators';
import { NavbarRefreshService } from '@app/shared/service/shared/navbar-refresh.service';

@Component({
    selector: 'app-favorite-tree',
    templateUrl: './favorite-tree.component.html',
    styleUrls: ['./favorite-tree.component.scss']
})
export class FavoriteTreeComponent implements OnInit {
    public inEditMode = false;
    public isSaving = false;
    public isLoading = false;
    public mainLanguageCode: string;

    public user: User;
    public favorites: Favorite[];

    constructor(private favoriteService: FavoriteService, private translateService: TranslateService,
                private navbarRefreshService: NavbarRefreshService, private route: ActivatedRoute) {
        this.mainLanguageCode = this.translateService.getDefaultLang();
        //this.navbarRefreshService.triggerRefresh();
    }

    public ngOnInit() {
        this.isSaving = false;
        this.user = this.route.snapshot.data.user;
        this.favorites = this.route.snapshot.data.favorites;
    }

    public saveFavorite(node: CategoryTreeNode): void {
        node.setLoading();
        const favorite = new Favorite();
        favorite.externalUser = this.user;
        favorite.resource = node.data;
        this.favoriteService
            .save(favorite)
            .pipe(finalize(() => node.unsetLoading()))
            .subscribe({
                next: () => this.updateFavorites(),
                error: () => this.updateFavorites()
            });
    }

    public deleteFavorite(node: CategoryTreeNode): void {
        node.setLoading();
        const favorite = this.favorites.find((fav) => fav.resource.id === node.data.id)!;
        this.favoriteService
            .delete(favorite.id)
            .pipe(finalize(() => node.unsetLoading()))
            .subscribe({
                next: () => this.updateFavorites(),
                error: () => this.updateFavorites()
            });
    }

    private updateFavorites() {
        this.favoriteService.findByUserId().subscribe((favorites) => {
            this.favorites = favorites;
        });
    }
}
