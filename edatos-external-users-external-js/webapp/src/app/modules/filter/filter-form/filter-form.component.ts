import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { ConfigService } from '@app/config';
import { Filter } from '@app/shared/model';
import { FilterService } from '@app/shared/service';
import {
    DeleteFilterDialogComponent
} from '@app/modules/filter/filter-form/delete-filter-dialog/delete-filter-dialog.component';
import { GenericModalService } from 'arte-ng/services';

@Component({
    selector: 'app-filter-form',
    templateUrl: './filter-form.component.html'
})
export class FilterFormComponent implements OnInit {
    public inEditMode = false;
    public isSaving = false;
    public isLoading = false;
    public filter: Filter;
    public account: any;

    public urlPermalink: boolean = false;

    constructor(
        private filterService: FilterService,
        private activatedRoute: ActivatedRoute,
        private titleService: Title,
        private router: Router,
        private configService: ConfigService,
        private genericModalService: GenericModalService
    ) {
        this.filter = this.activatedRoute.snapshot.data['filter'] ?? new Filter();
        this.activatedRoute.url.subscribe((segments) => {
            const lastUrlSegment = segments[segments.length - 1].path;
            this.inEditMode = this.inEditMode || lastUrlSegment === 'new';
        });

    }

    public ngOnInit() {
        this.account = this.activatedRoute.snapshot.data.user;
    }


    public edit(): void {
        this.router.navigate([`../${this.filter.id}/edit`], { relativeTo: this.activatedRoute });
    }

    public onCopyUrlPermalinkSuccess() {
        this.urlPermalink = true;
        setTimeout(() => {
            this.urlPermalink = false;
        }, 3000);
    }

    public openSelfLink(filter: Filter) {
        if (window._paq && window._paq.push && filter.externalOperation) {
            window._paq.push(["trackEvent", "eUsuarios", "ClickOperationLink", filter.externalOperation.code]);
        }
        window.open(filter.urlPermalink, '_blank');
    }

    public openDeleteDialog() {
        this.showSpinner();
        this.genericModalService.open(DeleteFilterDialogComponent as Component, { filter: this.filter }, { container: '.app' });
    }


    private showSpinner(): void {
        this.isLoading = true;
    }

}
