-- -----------------------------------------------------------------------------------------------
-- EDATOS-4109 - Problema con el inicio de sesión de e-Usuarios y creación de consultas personalizadas
-- -----------------------------------------------------------------------------------------------

-- Añadir propiedad con el número límite que diferencia a los bots (números menores al de la propiedad) de las personas (números mayores o iguales al de la propiedad) si el captcha en uso es el recaptcha
insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.threshold', 'FILL_ME', false);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.threshold', '0.5', false);
-- Ejemplo PRE ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.threshold', '0.5', false);
-- Ejemplo PRO ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.threshold', '0.5', false);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

commit;
