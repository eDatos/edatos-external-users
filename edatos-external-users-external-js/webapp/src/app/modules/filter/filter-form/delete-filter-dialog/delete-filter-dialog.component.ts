import { Component } from '@angular/core';
import { Filter } from '@app/shared/model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute, Router } from '@angular/router';
import { FilterService } from '@app/shared/service';

@Component({
    selector: 'app-delete-confirm-dialog',
    templateUrl: './delete-filter-dialog.component.html'
})
export class DeleteFilterDialogComponent {
    constructor(private activeModal: NgbActiveModal, private activatedRoute: ActivatedRoute,
                private router: Router, private filterService: FilterService) {
    }

    public filter: Filter;

    public confirm() {

        this.filterService
            .delete(this.filter.id)
            .subscribe((response) => {
                if (response.status >= 200 && response.status < 300) {
                    this.activeModal.close(true);
                    this.router.navigate([`..`], { relativeTo: this.activatedRoute });
                }
            });
    }

    public clear() {
        this.activeModal.dismiss(false);
        this.router.navigate([`../filter/${this.filter.id}`]);
    }
}
