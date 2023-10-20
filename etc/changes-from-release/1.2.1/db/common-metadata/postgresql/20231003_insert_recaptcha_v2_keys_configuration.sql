-- -----------------------------------------------------------------------------------------------
-- EDATOS-4242 - Proporcionar alternativa cuando el recaptcha considera al usuario un bot
-- -----------------------------------------------------------------------------------------------

-- Añadir propiedad con la clave privada de la versión 2 del recptcha
insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.v2.private_key', 'FILL_ME_WITH_PRIVATE_KEY', false);
-- Ejemplo DESARROLLO: insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.v2.private_key', '6LeMsVQoAAAAAHvsLBdfokM0Yqi7C9gwHmwMMJOw', false);
-- Ejemplo PRE ISTAC: insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.v2.private_key', null, false);
-- Ejemplo PRO ISTAC: insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.v2.private_key', null, false);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

-- Añadir propiedad con la clave pública de la versión 2 del recptcha
insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.v2.public_key', 'FILL_ME_WITH_PUBLIC_KEY', false);
-- Ejemplo DESARROLLO: insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.v2.public_key', '6LeMsVQoAAAAAGJAf0kujJ2DbJc4XfKT1Pavs9Fh', false);
-- Ejemplo PRE ISTAC: insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.v2.public_key', null, false);
-- Ejemplo PRO ISTAC: insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.v2.public_key', null, false);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

commit;
