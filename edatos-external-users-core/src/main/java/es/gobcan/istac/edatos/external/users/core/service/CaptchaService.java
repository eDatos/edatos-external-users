package es.gobcan.istac.edatos.external.users.core.service;

public interface CaptchaService {
    void saveResponse(String key, String response);
    Object getResponse(String key);
    boolean validateSimple(String userResponse, String key);
    boolean validateRecaptcha(String userResponse, String captchaAction);
}