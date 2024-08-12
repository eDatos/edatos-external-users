declare var Edatos: {
    captcha: {
        requestWithCaptcha: <T>(
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
        ) => Promise<T>,
        showCaptcha: (options: {
            captchaEl?: any,
            captchaId?: string,
            imgClasses?: string,
            inputClasses?: string,
            buttonClasses?: string,
            labelClasses?: string,
            labelText?: string,
            buttonText?: string,
            action?: string
        }) => void,
        showCaptchaWithButton: (options: {
            captchaEl?: any,
            captchaId?: string,
            imgClasses?: string,
            inputClasses?: string,
            buttonClasses?: string,
            labelClasses?: string,
            labelText?: string,
            buttonText?: string,
            action?: string
        }) => void
    }
};
