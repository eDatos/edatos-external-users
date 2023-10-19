-- -----------------------------------------------------------------------------------------------
-- EDATOS-4078 - Revisar la localización del archivo authentication.js
-- -----------------------------------------------------------------------------------------------

-- Actualiza la propiedad a la nueva localización del endpoint del captcha
UPDATE tb_data_configurations SET conf_value = 'FILL_ME' WHERE conf_key = 'metamac.portal.rest.external.authentication.captcha.url';
-- Ejemplo DESARROLLO: UPDATE tb_data_configurations SET conf_value = 'https://estadisticas.arte-consultores.com/external-users-external/captcha' WHERE conf_key = 'metamac.portal.rest.external.authentication.captcha.url';
-- Ejemplo DEMO: UPDATE tb_data_configurations SET conf_value = 'https://demo.edatos.io/external-users-external/captcha' WHERE conf_key = 'metamac.portal.rest.external.authentication.captcha.url';
-- Ejemplo PRE ISTAC: tb_data_configurations SET conf_value = 'https://www3-pre.gobiernodecanarias.org/aplicaciones/appsistac/external-users/captcha' WHERE conf_key = 'metamac.portal.rest.external.authentication.captcha.url';
-- Ejemplo PRO ISTAC: UPDATE tb_data_configurations SET conf_value = 'https://www3.gobiernodecanarias.org/aplicaciones/appsistac/external-users/captcha' WHERE conf_key = 'metamac.portal.rest.external.authentication.captcha.url';

commit;
