var showCaptcha = function () {
	return;
};

var showCaptchaWithButton = function (request, url, options) {
	return requestWithCaptcha(request, url, options);
};

var sendRequestWithCaptcha = function(request, baseUrl, recaptchaToken, action) {
    var url = new URL(baseUrl);
	if(isCaptchaEnabled()) {
	    url.searchParams.set('userValue', recaptchaToken);
		if(action) {
			url.searchParams.set('captchaAction', action);
		}
	}
    return request(url.href);
};

var isCaptchaEnabled = function() {
	return /*[[${captchaEnabled}]]*/ false;
};

var requestWithCaptcha = function(request, url, options) {
    return new Promise((resolve, reject) => {
		if(isCaptchaEnabled()) {
			grecaptcha.ready(function() {
                var captchaEl = options.captchaEl || document.getElementById(options.captchaId);
                const invisibleRecaptchaFailedAttribute = "invisible-recaptcha-failed";
                if (!captchaEl.getAttribute(invisibleRecaptchaFailedAttribute)) {
                    grecaptcha.execute((/*[[${recaptchaSiteKey}]]*/ ""), options.action ? {action: options.action} : {}).then(function (token) {
                        sendRequestWithCaptcha(request, url, token, options.action).then(function (response) {
                            resolve(response);
                        }).catch(function (error) {
                            var errorCode;
                            if (options.httpErrorBodyExtractor && options.httpErrorBodyExtractor(error).code) {
                                errorCode = options.httpErrorBodyExtractor(error).code;
                            } else if (error.error && error.error.code) {
                                errorCode = error.error.code;
                            } else if (error.responseText && JSON.parse(error.responseText).code) {
                                errorCode = JSON.parse(error.responseText).code;
                            }

                            if (errorCode === 'exception.external_users.captcha.unsuccessful') {
                                captchaEl.setAttribute(invisibleRecaptchaFailedAttribute, "true");
                                grecaptcha.render(captchaEl, {
                                    'sitekey': '6LeMsVQoAAAAAGJAf0kujJ2DbJc4XfKT1Pavs9Fh' // this
                                });
                            }
                            reject(error);
                        });
                    });
                } else {
                    sendRequestWithCaptcha(request, url, grecaptcha.getResponse()).then(function (response) {
                        resolve(response);
                        captchaEl.removeAttribute(invisibleRecaptchaFailedAttribute);
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

if(isCaptchaEnabled()) {
	var recaptchaScript = document.createElement("script");
    recaptchaScript.src = "https://www.google.com/recaptcha/api.js?hl=" + (/*[[${locale}]]*/ "") + "&render=" + (/*[[${recaptchaSiteKey}]]*/ "");
	document.head.appendChild(recaptchaScript);
}
