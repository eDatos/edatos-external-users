declare var requestWithCaptcha: <T>(
    request: (url: String) => Promise<T>,
    url: string,
    options: {
        captchaEl?: any,
        captchaId?: string,
        imgClasses?: string,
        inputClasses?: string,
        buttonClasses?: string,
        labelClasses?: string,
        labelText?: string,
        buttonText?: string,
        action?: string
    }
) => Promise<T>;

declare var showCaptcha: (options: {
    captchaEl?: any,
    captchaId?: string,
    imgClasses?: string,
    inputClasses?: string,
    buttonClasses?: string,
    labelClasses?: string,
    labelText?: string,
    buttonText?: string,
    action?: string
}) => void;

declare var Edatos: {
    UserManagement: {
        refreshAuthenticationState: () => void,
        addOnLogoutListener: (callback: () => void) => void
    }
}
