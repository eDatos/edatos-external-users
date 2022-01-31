import { ElementRef } from "@angular/core";

export function reinsertScripts(el: ElementRef) {
    const scriptList = el.nativeElement.getElementsByTagName('script');
    for (const script of scriptList) {
        const scriptCopy = document.createElement('script');
        if (script.innerHTML) {
            scriptCopy.innerHTML = script.innerHTML;
        } else if (script.src) {
            scriptCopy.src = script.src;
        }
        scriptCopy.async = false;
        script.parentNode.replaceChild(scriptCopy, script);
    }
}
