import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FilterFormEditComponent } from './filter-form-edit.component';

describe('FilterFormComponent', () => {
    let component: FilterFormEditComponent;
    let fixture: ComponentFixture<FilterFormEditComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [FilterFormEditComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(FilterFormEditComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
