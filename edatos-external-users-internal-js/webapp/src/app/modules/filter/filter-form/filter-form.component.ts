import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { ExternalUserService } from '@app/core/service';
import { Filter } from '@app/shared/model';
import { FilterService } from '@app/shared/service';

@Component({
    selector: 'app-filter-form',
    templateUrl: './filter-form.component.html',
})
export class FilterFormComponent implements OnInit {
    public filter: Filter;
    public userId: number;

    constructor(
        private filterService: FilterService,
        private activatedRoute: ActivatedRoute,
        private titleService: Title,
        private router: Router,
        private externalUserService: ExternalUserService
    ) {
        this.filter = this.activatedRoute.snapshot.data['filter'] ?? new Filter();
        this.userId = parseInt(this.activatedRoute.snapshot.queryParams['id'], 10);
        this.loadExternalUser();
    }

    public ngOnInit() {
        this.titleService.setTitle(this.titleService.getTitle() + ' ' + this.filter.id);
    }

    public goBack(): void {
        window.history.back();
    }

    private loadExternalUser() {
        if (this.userId) {
            this.externalUserService.get(this.userId).subscribe((result) => this.filter.externalUser = result);
        }
    }
}
