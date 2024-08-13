window.Edatos = window.Edatos || {};

(function () {
    var internationalizationCookieId = /*[[${internationalizationCookieId}]]*/ "chosenLocale";
    var defaultLanguage = /*[[${defaultLanguage}]]*/ "es";
    var chosenLocale = document.cookie.replace(new RegExp("(?:(?:^|.*;\\s*)" + internationalizationCookieId + "\\s*\\=\\s*([^;]*).*$)|^.*$"), "$1") || defaultLanguage;
    var captchaEnabled = /*[[${captchaEnabled}]]*/ false;
    var captchaGeneratedId = null;
    var labelTranslations = {
        es: "Escriba el resultado de la operación",
        en: "Enter the result of the operation",
        ca: "Escriviu el resultat de l'operació"
    };
    var buttonTranslations = {
        es: "Enviar", en: "Send", ca: "Enviar"
    };

    var createCaptchaSessionKey = function() {
        return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c => (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
        )
    };

    var removeCaptcha = function(options) {
        if(options.captchaEl) {
            var captchaContainerEl = options.captchaEl.querySelector("#captcha-container");
            if(captchaContainerEl) {
                options.captchaEl.removeChild(captchaContainerEl);
            }
        }
    }

    var sendRequestWithCaptcha = function(request, baseUrl, options) {
        var url = new URL(baseUrl);
        var captchaInput = options.captchaEl && options.captchaEl.querySelector("#captcha-container input");
        var userValue = captchaInput && captchaInput.value;
        if(captchaEnabled && userValue && captchaGeneratedId) {
            url.searchParams.set('userValue', userValue);
            url.searchParams.set('sessionKey', captchaGeneratedId);
        }
        return request(url.href);
    };

    Edatos.captcha = {
        showCaptcha: function (options) {
            if(captchaEnabled) {
                captchaGeneratedId = createCaptchaSessionKey();
                var imgUrl = new URL(/*[[${captchaPictureUrl}]]*/ "");
                imgUrl.searchParams.set('sessionKey', captchaGeneratedId);

                if(!options.captchaEl && options.captchaId) {
                    options.captchaEl = document.getElementById(options.captchaId);
                }
                removeCaptcha(options);
                options.captchaEl.insertAdjacentHTML('beforeend', `
                <div id="captcha-container" class="captcha captcha-gobcan">
                    <img src="${imgUrl.href}">
                    <label for="code"></label>
                    <input type="text" name="code" id="code"/>
                </div>`);

                options.captchaEl.querySelector("#captcha-container img").className = options.imgClasses ? options.imgClasses : "captcha-img";
                options.captchaEl.querySelector("#captcha-container input").className = options.inputClasses ? options.inputClasses : "captcha-input";
                var labelEl = options.captchaEl.querySelector("#captcha-container label");
                labelEl.className = options.labelClasses ? options.labelClasses : "captcha-label";

                if (!options.labelText) {
                    options.labelText = labelTranslations[chosenLocale] || labelTranslations[defaultLanguage];
                }
                labelEl.innerText = options.labelText;
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
            if(!options.captchaEl && options.captchaId) {
                options.captchaEl = document.getElementById(options.captchaId);
            }
            return new Promise((resolve, reject) => {
                sendRequestWithCaptcha(request, url, options).then(function (response) {
                    removeCaptcha(options);
                    resolve(response);
                }).catch(function (error) {
                    if(captchaEnabled) {
                        removeCaptcha(options);
                        if(options.withButton) {
                            Edatos.captcha.showCaptchaWithButton(request, url, options).then(val => resolve(val)).catch(error => reject(error));
                        } else {
                            Edatos.captcha.showCaptcha(options);
                            reject(error);
                        }
                    } else {
                        reject(error);
                    }
                });
            });
        }
    };
})();
