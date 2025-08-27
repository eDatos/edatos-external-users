import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { buildProvider } from 'arte-ng/utils';
import { AutocompleteComponent } from './autocomplete.component';

/*
    Documentación en http://confluence.arte-consultores.com/display/INFRASTR/ac-autocomplete-long-list
*/
@Component({
    selector: 'app-autocomplete-long-list',
    templateUrl: 'autocomplete.component.html',
    providers: [buildProvider(AutocompleteLongListComponent)]
})
export class AutocompleteLongListComponent extends AutocompleteComponent implements OnInit {
    constructor(protected translateService: TranslateService) {
        super(translateService);
        this.minLength = 3;
    }

    ngOnInit() {
        if (!(this.completeMethod.observers.length > 0)) {
            throw new Error('completeMethod is required on arte-autocomplete-long-list');
        }
        this.placeholder = this.translateService.instant('arte.autocomplete-long-list.placeholder');
        super.ngOnInit();
    }
}
