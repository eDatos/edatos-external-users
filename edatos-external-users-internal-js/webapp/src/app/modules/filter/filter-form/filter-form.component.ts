import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { ExternalUserService } from '@app/core/service';
import { Filter } from '@app/shared/model';
import { FilterService } from '@app/shared/service';
import { ConfigService } from '@app/config';

@Component({
    selector: 'app-filter-form',
    templateUrl: './filter-form.component.html',
})
export class FilterFormComponent implements OnInit {
    public filter: Filter;
    public userId: number;
    public simplifiedSignUp: boolean = false;

    constructor(
        private filterService: FilterService,
        private activatedRoute: ActivatedRoute,
        private titleService: Title,
        private router: Router,
        private externalUserService: ExternalUserService,
        private configService: ConfigService,
    ) {
        this.simplifiedSignUp = this.configService.getConfig().metadata.simplifiedSignUp;
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
