import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { ConfigService } from '@app/config';
import { Filter } from '@app/core/model';
import { FilterService } from '@app/shared/service';
import { finalize } from 'rxjs/operators';

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
        private configService: ConfigService
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

    public openSelfLink(selfLink: string) {
        window.open(selfLink, '_blank');
    }

    public delete() {
        this.showSpinner();
        this.filterService
            .delete(this.filter.id)
            .pipe(finalize(() => this.hideSpinner()))
            .subscribe((response) => {
                if (response.status >= 200 && response.status < 300) {
                    this.router.navigate([`..`], { relativeTo: this.activatedRoute });
                }
            });
    }


    private showSpinner(): void {
        this.isLoading = true;
    }

    private hideSpinner(): void {
        this.isLoading = false;
    }
}
