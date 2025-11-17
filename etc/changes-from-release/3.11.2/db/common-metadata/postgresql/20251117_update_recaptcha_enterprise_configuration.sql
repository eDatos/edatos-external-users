-- -----------------------------------------------------------------------------------------------
-- EDATOS-4826 - Migrar claves del recaptcha a un proyecto de Google Cloud
-- -----------------------------------------------------------------------------------------------

-- Eliminar las propiedades antiguas de las claves privadas (secret keys) que ya no son necesarias con reCAPTCHA Enterprise
DELETE FROM TB_DATA_CONFIGURATIONS WHERE CONF_KEY = 'metamac.portal.rest.external.authentication.captcha.private_key';
DELETE FROM TB_DATA_CONFIGURATIONS WHERE CONF_KEY = 'metamac.portal.rest.external.authentication.captcha.v2.private_key';

-- Añadir propiedad con las credenciales JSON de la cuenta de servicio para reCAPTCHA Enterprise. Ver documentación del captcha para más detalles sobre cómo rellenar esta propiedad.
insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.credentials', 'FILL_ME_WITH_CREDENTIALS', false);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

-- Añadir propiedad con el nombre de proyecto de Google Cloud asociado a reCAPTCHA Enterprise. Ver documentación del captcha para más detalles sobre cómo rellenar esta propiedad.
insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.project_name', 'FILL_ME_WITH_PROJECT_NAME', false);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

commit;
