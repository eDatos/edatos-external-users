import {ChangeDetectorRef, Component, ElementRef, EventEmitter, forwardRef, HostBinding, Input, NgZone, OnInit, Output, Renderer2, ViewChild} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';
import {TranslateService} from '@ngx-translate/core';
import {Calendar} from 'primeng/calendar';
import {LocaleSettings} from 'primeng/calendar/calendar';

@Component({
    selector: 'app-custom-calendar',
    templateUrl: './custom-calendar.component.html',
    styleUrls: ['./custom-calendar.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            multi: true,
            useExisting: forwardRef(() => CustomCalendarComponent)
        }
    ]
})
export class CustomCalendarComponent extends Calendar implements ControlValueAccessor, OnInit {

    @Input()
    public get date(): any {
        return this._date;
    }

    public set date(value) {
        this._date = value;
        this.onModelChange(this._date);
    }

    public _date;

    @Input()
    public dateFormat = 'dd/mm/yy';

    public customLocaleProperties: LocaleSettings;

    @Input()
    public name = 'calendar';

    @Input()
    public required = false;

    @Input()
    public minDate: Date;

    @Input()
    public maxDate: Date;

    @Input()
    public selectionMode: 'single' | 'range' | 'multiple' = 'single';

    @Input()
    public dataType: 'string' | 'date' = 'string';

    @Input()
    public showTime: boolean;

    @Input()
    public inputId: string;

    @Input()
    @HostBinding('class.disabled')
    public disabled = false;

    @Output()
    public onSelect: EventEmitter<any> = new EventEmitter();

    @Output()
    public onInput: EventEmitter<any> = new EventEmitter();

    public onModelChange: (any?) => {};

    @ViewChild(Calendar)
    private calendar: Calendar;

    constructor(private translateService: TranslateService, el: ElementRef, renderer: Renderer2, cd: ChangeDetectorRef, zone: NgZone) {
        super(el, renderer, cd, zone);
    }

    public onModelTouched: (any?) => void = () => {};

    public onSelectMethod($event) {
        this.onSelect.emit($event);
        this.onModelChange(this._date);
    }

    public onInputMethod($event) {
        if (this.date) {
            this.onInput.emit($event);
            this.onModelChange(this._date);
        }
    }

    public registerOnChange(fn: any): void {
        this.onModelChange = fn;
    }

    public registerOnTouched(fn: any): void {
        this.onModelTouched = fn;
    }

    public writeValue(value: any): void {
        this._date = value ? value : undefined;
    }

    public ngOnInit() {
        this.customLocaleProperties = this.locale;
        this.customLocaleProperties.firstDayOfWeek = 1;
        this.customLocaleProperties.dateFormat = this.dateFormat;
        this.customLocaleProperties.monthNames = this.customMonthNames();
        this.customLocaleProperties.dayNamesMin = this.customDayNamesMin();
        this.setPropertiesDate();
    }

    private setPropertiesDate() {
        const today: Date = new Date();
        this.minDate = new Date(today.getFullYear() - 100, today.getMonth(), today.getDate());
        this.maxDate = new Date(today.getFullYear(), today.getMonth(), today.getDate());
        this.yearRange = this.minDate.getFullYear() + ':' + today.getFullYear();
    }

    public checkYear(date: { month: number; year: number }) {
        if (this.calendar) {
            if (date.year < this.minDate.getFullYear()) {
                this.calendar.onModelTouched();
                this.date = new Date(this.minDate);
                this.calendar.yearRange = this.minDate.getFullYear() + ':' + this.maxDate.getFullYear();
            }
            if (date.year > this.maxDate.getFullYear()) {
                this.calendar.onModelTouched();
                let lastDayOfYear = new Date(this.maxDate.getFullYear(), 11, 31);
                this.date = new Date(lastDayOfYear.valueOf());
                this.calendar.yearRange = this.minDate.getFullYear() + ':' + this.maxDate.getFullYear();
            }
        }
    }

    private customMonthNames(): string[] {
        return [this.translateService.instant('calendar.monthNames.january'),
            this.translateService.instant('calendar.monthNames.february'),
            this.translateService.instant('calendar.monthNames.march'),
            this.translateService.instant('calendar.monthNames.april'),
            this.translateService.instant('calendar.monthNames.may'),
            this.translateService.instant('calendar.monthNames.june'),
            this.translateService.instant('calendar.monthNames.july'),
            this.translateService.instant('calendar.monthNames.august'),
            this.translateService.instant('calendar.monthNames.september'),
            this.translateService.instant('calendar.monthNames.october'),
            this.translateService.instant('calendar.monthNames.november'),
            this.translateService.instant('calendar.monthNames.december')];
    }

    private customDayNamesMin(): string[] {
        return [this.translateService.instant('calendar.dayNamesMin.sunday')
            , this.translateService.instant('calendar.dayNamesMin.monday'),
            this.translateService.instant('calendar.dayNamesMin.tuesday'),
            this.translateService.instant('calendar.dayNamesMin.wednesday'),
            this.translateService.instant('calendar.dayNamesMin.thursday'),
            this.translateService.instant('calendar.dayNamesMin.friday'),
            this.translateService.instant('calendar.dayNamesMin.saturday')];
    }

}
