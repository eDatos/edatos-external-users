import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { ConfigService } from '@app/config';
import { Filter } from '@app/core/model';
import { FilterService } from '@app/shared/service';
import { finalize } from 'rxjs/operators';

@Component({
    selector: 'app-filter-form',
    templateUrl: './filter-form-edit.component.html'
})
export class FilterFormEditComponent implements OnInit {
    public inEditMode = false;
    public isSaving = false;
    public isLoading = false;
    public filter: Filter;
    public account: any;

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

    public cancel(): void {
        this.router.navigate([`../filter/${this.filter.id}`]);
    }


    public submit(): void {
        this.toggleIsSaving();

        if (this.filter.id == null) {
            this.filter.externalUser = this.account;
            this.filterService
                .save(this.filter)
                .pipe(finalize(() => this.toggleIsSaving()))
                .subscribe((filter) => {
                    this.router.navigate([`../${filter.id}`], { relativeTo: this.activatedRoute });
                });
        } else {
            this.filterService
                .update(this.filter)
                .pipe(finalize(() => this.toggleIsSaving()))
                .subscribe((filter) => {
                    this.filter = filter;
                });
        }
    }

    private toggleIsSaving(): void {
        this.isSaving = !this.isSaving;
    }

}
