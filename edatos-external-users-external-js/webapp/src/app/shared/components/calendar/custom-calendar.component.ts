import {
    ChangeDetectorRef,
    Component,
    ElementRef,
    EventEmitter,
    forwardRef,
    HostBinding,
    Input,
    NgZone,
    OnInit,
    Output,
    Renderer2,
    ViewChild
} from '@angular/core';
import { Calendar } from 'primeng/calendar';
import { TranslateService } from '@ngx-translate/core';
import { LocaleSettings } from 'primeng/calendar/calendar';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

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

    _date;

    @Input()
    public dateFormat = 'dd/mm/yy';

    public customLocaleProperties: LocaleSettings;

    @Input()
    name = 'calendar';

    @Input()
    required = false;

    @Input()
    minDate: Date;

    @Input()
    maxDate: Date;

    @Input()
    showTime: boolean;

    @Input()
    inputId: string;

    @Input()
    @HostBinding('class.disabled')
    disabled = false;

    @Output()
    public onSelect: EventEmitter<any> = new EventEmitter();

    @Output()
    public onInput: EventEmitter<any> = new EventEmitter();

    @ViewChild(Calendar)
    private calendar: Calendar;

    public onModelChange: (any?) => {};

    public onModelTouched: (any?) => void = () => {
    };


    constructor(private translateService: TranslateService, el: ElementRef, renderer: Renderer2, cd: ChangeDetectorRef, zone: NgZone) {
        super(el, renderer, cd, zone);
    }

    ngOnInit() {
        this.customLocaleProperties = this.locale;
        this.customLocaleProperties.firstDayOfWeek = 1;
        this.customLocaleProperties.dateFormat = this.dateFormat;
        this.customLocaleProperties.monthNames = this.customMonthNames();
        this.customLocaleProperties.dayNamesMin = this.customDayNamesMin();
        this.setPropertiesDate();

    }

    onSelectMethod($event) {
        this.onSelect.emit($event);
        this.onModelChange(this._date);
    }

    onInputMethod($event) {
        if (this.date) {
            this.onInput.emit($event);
            this.onModelChange(this._date);
        }
    }

    writeValue(value: any): void {
        this._date = value ? new Date(value) : undefined;
    }

    @Input()
    get date(): any {
        return this._date;
    }

    set date(value) {
        this._date = value;
        this._date.setHours(1);
        this.onModelChange(this._date);
    }

    registerOnChange(fn: any): void {
        this.onModelChange = fn;
    }

    registerOnTouched(fn: any): void {
        this.onModelTouched = fn;
    }

    private setPropertiesDate() {
        let today: Date = new Date();
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
