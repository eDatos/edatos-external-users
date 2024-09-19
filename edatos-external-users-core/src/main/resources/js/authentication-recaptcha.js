window.Edatos = window.Edatos || {};

(function () {
    /* VARIABLES */

    var internationalizationCookieId = /*[[${internationalizationCookieId}]]*/ "chosenLocale";
    var defaultLanguage = /*[[${defaultLanguage}]]*/ "es";
    var chosenLocale = document.cookie.replace(new RegExp("(?:(?:^|.*;\\s*)" + internationalizationCookieId + "\\s*\\=\\s*([^;]*).*$)|^.*$"), "$1") || defaultLanguage;
    var captchaEnabled = /*[[${captchaEnabled}]]*/ false;
    var recaptchaSiteKey = /*[[${recaptchaSiteKey}]]*/ "";
    var recaptchaV2SiteKey = /*[[${recaptchaV2SiteKey}]]*/ "";
    var recaptchaTranslations = {
        es: 'Este sitio está protegido por reCAPTCHA y se aplican la ' +
            '<a href="https://policies.google.com/privacy?hl=es">Política de Privacidad</a> y los ' +
            '<a href="https://policies.google.com/terms?hl=es">Términos del Servicio</a> de Google.'
        ,
        en: 'This site is protected by reCAPTCHA and the Google ' +
            '<a href="https://policies.google.com/privacy?hl=en">Privacy Policy</a> and ' +
            '<a href="https://policies.google.com/terms?hl=en">Terms of Service</a> apply.'
        ,
        ca: 'Aquest lloc està protegit per reCAPTCHA i s\'apliquen la ' +
            '<a href="https://policies.google.com/privacy?hl=ca">Política de Privadesa</a> i les ' +
            '<a href="https://policies.google.com/terms?hl=ca">Condicions del servei</a> de Google.'
    };
    var buttonTranslations = {
        es: 'Continuar', en: 'Continue', ca: 'Continuar'
    };

    /* PRIVATE FUNCTIONS */

    var removeCaptcha = function(options) {
        if (options.captchaEl) {
            var captchaContainerEl = options.captchaEl.querySelector("#captcha-container");
            if (captchaContainerEl) {
                options.captchaEl.removeChild(captchaContainerEl);
            }
        }
    }

    var sendRequestWithCaptcha = function(request, baseUrl, recaptchaToken, action) {
        var url = new URL(baseUrl);
        if(captchaEnabled) {
            url.searchParams.set('userValue', recaptchaToken);
            if(action) {
                url.searchParams.set('captchaAction', action);
            }
        }
        return request(url.href);
    };

    /* PUBLIC FUNCTIONS */

    Edatos.captcha = {
        showCaptcha: function (options) {
            if(captchaEnabled) {
                if(!options.captchaEl && options.captchaId) {
                    options.captchaEl = document.getElementById(options.captchaId);
                }
                removeCaptcha(options);

                var recaptchaText = recaptchaTranslations[chosenLocale] || recaptchaTranslations[defaultLanguage];
                options.captchaEl.insertAdjacentHTML('beforeend', `
                    <div id="captcha-container" class="captcha-recaptcha">
                        <label>${recaptchaText}</label>
                    </div>
                `);

                var labelEl = options.captchaEl.querySelector("#captcha-container label");
                labelEl.className = options.labelClasses ? options.labelClasses : "captcha-label";
            }
        },

        showCaptchaWithButton: function (request, url, options) {
            if(captchaEnabled) {
                return new Promise((resolve, reject) => {
                    Edatos.captcha.showCaptcha(options);
                    options.withButton = true;

                    var $button = document.createElement("button");
                    $button.className = options.buttonClasses ? options.buttonClasses : "captcha-button";

                    if (!options.buttonText) {
                        options.buttonText = buttonTranslations[chosenLocale] || buttonTranslations[defaultLanguage];
                    }
                    $button.textContent = options.buttonText;

                    options.captchaEl.querySelector("#captcha-container").appendChild($button);
                    $button.addEventListener("click", function (e) {
                        e.preventDefault();
                        Edatos.captcha.requestWithCaptcha(request, url, options).then(val => resolve(val)).catch(error => reject(error));
                    }, false);
                });
            } else {
                return Edatos.captcha.requestWithCaptcha(request, url, options);
            }
        },

        requestWithCaptcha: function(request, url, options) {
            return new Promise((resolve, reject) => {
                if(captchaEnabled) {
                    grecaptcha.ready(function() {
                        var captchaEl = options.captchaEl || document.getElementById(options.captchaId);
                        const invisibleRecaptchaFailedAttribute = "invisible-recaptcha-failed";
                        if (!captchaEl.getAttribute(invisibleRecaptchaFailedAttribute)) {
                            grecaptcha.execute(recaptchaSiteKey, options.action ? {action: options.action} : {}).then(function (token) {
                                sendRequestWithCaptcha(request, url, token, options.action).then(function (response) {
                                    removeCaptcha(options);
                                    resolve(response);
                                }).catch(function (error) {
                                    var errorCode;
                                    if (options.httpErrorBodyExtractor && options.httpErrorBodyExtractor(error).code) {
                                        errorCode = options.httpErrorBodyExtractor(error).code;
                                    } else if (error.error && error.error.code) {
                                        errorCode = error.error.code;
                                    } else if (error.responseText) {
                                        try {
                                            errorCode = JSON.parse(error.responseText).code;
                                        } catch (e) {
                                            errorCode = undefined;
                                        }
                                    }

                                    if (errorCode === 'exception.external_users.captcha.unsuccessful') {
                                        captchaEl.setAttribute(invisibleRecaptchaFailedAttribute, "true");
                                        removeCaptcha(options);
                                        captchaEl.insertAdjacentHTML('beforeend', `
                                            <div id="captcha-container" class="captcha-recaptcha"></div>
                                        `);
                                        grecaptcha.render(captchaEl.querySelector("#captcha-container"), {
                                            'sitekey': recaptchaV2SiteKey,
                                            'callback': function () {
                                                if (options.withButton) {
                                                    Edatos.captcha.requestWithCaptcha(request, url, options).then(val => resolve(val)).catch(error => reject(error))
                                                }
                                            }
                                        });
                                        if (!options.withButton) {
                                            reject(error);
                                        }
                                    } else {
                                        reject(error);
                                    }
                                });
                            });
                        } else {
                            sendRequestWithCaptcha(request, url, grecaptcha.getResponse()).then(function (response) {
                                resolve(response);
                                captchaEl.removeAttribute(invisibleRecaptchaFailedAttribute);
                                removeCaptcha(options);
                                grecaptcha.reset();
                            }).catch(function (error) {
                                grecaptcha.reset();
                                reject(error);
                            });
                        }
                    });
                } else {
                    sendRequestWithCaptcha(request, url).then(function (response) {
                        resolve(response);
                    }).catch(function (error) {
                        reject(error);
                    });
                }
            });
        }

    };

    /* INITIALIZATION */

    if(captchaEnabled) {
        var recaptchaScript = document.createElement("script");
        recaptchaScript.src = "https://www.google.com/recaptcha/api.js?hl=" + chosenLocale + "&render=" + recaptchaSiteKey;
        document.head.appendChild(recaptchaScript);

        var recaptchaStyle = document.createElement("style");
        recaptchaStyle.innerText = ".grecaptcha-badge { visibility: hidden; }";
        document.head.appendChild(recaptchaStyle);
    }
})();
