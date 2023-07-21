import { Component, forwardRef, Input, QueryList, ViewChildren } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { InternationalString } from '@app/shared/model';
import { Editor } from 'primeng/editor';

@Component({
  selector: 'app-multi-language-editor',
  templateUrl: './multi-language-editor.component.html',
  styleUrls: ['./multi-language-editor.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: forwardRef(() => MultiLanguageEditorComponent),
    }
  ]
})
export class MultiLanguageEditorComponent implements ControlValueAccessor {

  onTouched: any = () => {};
  onChange: any = () => {};
  isDisabled: boolean = false;
  editors: any = {};
  value: InternationalString = new InternationalString();

  @Input()
  locales: String[] = [];

  @Input()
  isEditMode: boolean = true;

  @ViewChildren(Editor) set setEditors(editors: QueryList<Editor>) {
    if(editors) {
      editors.forEach(editor => {
        this.editors[editor.el.nativeElement.id] = editor.quill;
      });
    }
  }

  constructor() {
    this.value.texts = [];
  }

  writeValue(value: InternationalString): void {
    this.value = value || new InternationalString();
    this.value.texts = this.value.texts || [];
    this.value.texts.forEach(value => {
      this.editors[value.locale]?.setContents(this.editors[value.locale].clipboard.convert(value.label));
    });
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }


  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    this.isDisabled = isDisabled; 
    this.editors?.forEach(editor => {
      (this.isDisabled || !this.isEditMode) ? editor.disable() : editor.enable();
    });
  }

  onEditorInit(event: any, locale: string) {
    (this.isDisabled || !this.isEditMode) ? event.editor.disable() : event.editor.enable();
    event.editor.setContents(event.editor.clipboard.convert(this.value.getLocalisedLabel(locale) || ""));
  }

  onEditorChange(event: any, locale: string) {
    this.value.texts = this.value.texts.map(text => {
      if (text.locale === locale) text.label = event.htmlValue;
      return text;
    });
    this.onChange(this.value);
  }

  onEditorTouched(event: any) {
    this.onTouched();
  }
}