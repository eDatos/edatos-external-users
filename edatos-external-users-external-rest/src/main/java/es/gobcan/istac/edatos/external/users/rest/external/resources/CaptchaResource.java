package es.gobcan.istac.edatos.external.users.rest.external.resources;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import es.gobcan.istac.edatos.external.users.core.config.CaptchaConstants;
import es.gobcan.istac.edatos.external.users.core.config.MetadataProperties;
import es.gobcan.istac.edatos.external.users.core.errors.CaptchaUnsuccessfulException;
import es.gobcan.istac.edatos.external.users.core.i18n.LocaleContext;
import liquibase.util.BooleanUtils;
import nl.captcha.Captcha;
import nl.captcha.servlet.CaptchaServletUtil;

@Controller
@RequestMapping(CaptchaResource.BASE_URL)
public class CaptchaResource extends AbstractResource {

    public static final String                                                BASE_URL = "/captcha";

    @Autowired
    private es.gobcan.istac.edatos.external.users.core.service.CaptchaService captchaService;

    @Autowired
    private MetadataProperties                                                metadataService;

    @GetMapping("/api/validate")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void validateCaptcha(@RequestParam(required = false) String userValue, @RequestParam(required = false) String sessionKey,
            @RequestParam(required = false) String captchaAction) {

        boolean captchaEnabled = metadataService.isCaptchaEnable();
        String captchaProvider = metadataService.getCaptchaProvider();
        if (!BooleanUtils.isTrue(captchaEnabled)) {
            return;
        } else if(userValue == null) {
            throw new CaptchaUnsuccessfulException();
        }

        boolean valid = false;
        if (CaptchaConstants.CAPTCHA_PROVIDER_RECAPTCHA.equals(captchaProvider)) {
            valid = captchaService.validateRecaptcha(userValue, captchaAction);
        } else if (CaptchaConstants.CAPTCHA_PROVIDER_SIMPLE.equals(captchaProvider)) {
            valid = captchaService.validateSimple(userValue, sessionKey);
        }

        if (!valid) {
            throw new CaptchaUnsuccessfulException();
        }
    }

    @GetMapping("/picture/simple")
    public void drawSimpleCaptcha(HttpServletRequest request, HttpServletResponse response, @RequestParam String sessionKey,
            @RequestParam(required = false, defaultValue = "" + CaptchaConstants.CAPTCHA_SIMPLE_WIDTH) int width,
            @RequestParam(required = false, defaultValue = "" + CaptchaConstants.CAPTCHA_SIMPLE_HEIGHT) int height) {

        Captcha captcha = new Captcha.Builder(width, height).addText().addBackground().addNoise().gimp().addBorder().build();

        captchaService.saveResponse(sessionKey, captcha.getAnswer());
        CaptchaServletUtil.writeImage(response, captcha.getImage());
    }

    @GetMapping(value="/captcha.js")
    public String authentication(HttpServletRequest request, Model model) {

        String captchaProvider = metadataService.getCaptchaProvider();
        boolean captchaEnable = metadataService.isCaptchaEnable();
        model.addAttribute(CaptchaConstants.CAPTCHA_ENABLED_MODEL_ATTR, captchaEnable);
        model.addAttribute(CaptchaConstants.CAPTCHA_INTERNATIONALIZATION_COOKIE_ID, metadataService.getLanguageCookie());
        model.addAttribute(CaptchaConstants.CAPTCHA_DEFAULT_LANGUAGE, metadataService.getDefaultLanguage());

        UriBuilder captchaPictureUri = UriBuilder.fromUri(metadataService.getCaptchaUrl());
        if (CaptchaConstants.CAPTCHA_PROVIDER_SIMPLE.equals(captchaProvider)) {
            model.addAttribute(CaptchaConstants.CAPTCHA_PICTURE_MODEL_ATTR, captchaPictureUri.path("/picture/simple").build().toString());
            return "authentication-simple";
        } else if (CaptchaConstants.CAPTCHA_PROVIDER_RECAPTCHA.equals(captchaProvider)) {
            model.addAttribute(CaptchaConstants.RECAPTCHA_SITE_KEY_MODEL_ATTR, metadataService.getRecaptchaSiteKey());
            model.addAttribute(CaptchaConstants.RECAPTCHA_V2_SITE_KEY_MODEL_ATTR, metadataService.getRecaptchaV2SiteKey());
            model.addAttribute("locale", LocaleContext.getLocale() == null ? LocaleContext.getDefaultLocale().toString() : LocaleContext.getLocale().toString());
            return "authentication-recaptcha";
        }
        return null;
    }
}
