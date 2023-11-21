-- --------------------------------------------------------------------------------------------------
-- EDATOS-3304 - Entregas ISTAC 2021 - edatos (entrega external-users)
-- --------------------------------------------------------------------------------------------------

insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.db.url', 'jdbc:postgresql://FILL_ME_WITH_HOST:FILL_ME_WITH_PORT/FILL_ME_WITH_DB', false);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.db.url', 'jdbc:postgresql://localhost:5432/external_users-bd', false);
-- Ejemplo PRE ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.db.url', 'jdbc:postgresql://PGPREEDATOS:7036/external_users_bd', false);
-- Ejemplo PRO ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.db.url', 'jdbc:postgresql://PGPROEDATOS:7042/external_users_bd', false);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.db.username', 'FILL_ME_WITH_USER', false);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.db.username', 'external_users-bd', false);
-- Ejemplo PRE ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.db.username', 'external_users_bd', false);
-- Ejemplo PRO ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.db.username', 'external_users_bd', false);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.db.password', 'FILL_ME_WITH_ENCRYPTED_PASSWORD', false);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.db.password', 'FILL_ME_WITH_ENCRYPTED_PASSWORD', false);
-- Ejemplo PRE ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.db.password', 'FILL_ME_WITH_ENCRYPTED_PASSWORD', false);
-- Ejemplo PRO ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.db.password', 'FILL_ME_WITH_ENCRYPTED_PASSWORD', false);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.db.driver_name', 'org.postgresql.Driver', false);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.rest.external', 'FILL_ME', true);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.rest.external', 'https://estadisticas.arte-consultores.com/external-users-external', true);
-- Ejemplo PRE ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.rest.external', 'https://www3-pre.gobiernodecanarias.org/aplicaciones/appsistac/external-users', true);
-- Ejemplo PRO ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.rest.external', 'https://www3.gobiernodecanarias.org/aplicaciones/appsistac/external-users', true);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.web.external.url', 'FILL_ME', true);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.web.external.url', 'https://estadisticas.arte-consultores.com/external-users-external', true);
-- Ejemplo PRE ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.web.external.url', 'https://www3-pre.gobiernodecanarias.org/aplicaciones/appsistac/external-users', true);
-- Ejemplo PRO ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.web.external.url', 'https://www3.gobiernodecanarias.org/aplicaciones/appsistac/external-users', true);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.url', 'FILL_ME', true);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.url', 'https://estadisticas.arte-consultores.com/external-users-external/captcha', true);
-- Ejemplo PRE ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.url', 'https://www3-pre.gobiernodecanarias.org/aplicaciones/appsistac/external-users/captcha', true);
-- Ejemplo PRO ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.url', 'https://www3.gobiernodecanarias.org/aplicaciones/appsistac/external-users/captcha', true);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.provider', 'FILL_ME', false);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.provider', 'recaptcha', false);
-- Ejemplo PRE ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.provider', 'gobcan', false);
-- Ejemplo PRO ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.provider', 'gobcan', false);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.enable', 'FILL_ME', false);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.enable', 'true', false);

UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.private_key', 'FILL_ME_WITH_PRIVATE_KEY', false);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.private_key', 'FILL_ME_WITH_PRIVATE_KEY', false);
-- Ejemplo PRE ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.private_key', null, false);
-- Ejemplo PRO ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.private_key', null, false);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.public_key', 'FILL_ME_WITH_PUBLIC_KEY', false);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.public_key', '6LeK1dcbAAAAAD-UW5qQc-VrHJbsVHgxc2Jer-IR', false);
-- Ejemplo PRE ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.public_key', null, false);
-- Ejemplo PRO ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.public_key', null, false);
UPDATE TB_SEQUENCES SET <SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.threshold', 'FILL_ME', false);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.threshold', '0.5', false);
-- Ejemplo PRE ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.threshold', '0.5', false);
-- Ejemplo PRO ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.threshold', '0.5', false);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.v2.private_key', 'FILL_ME_WITH_PRIVATE_KEY', false);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.v2.private_key', '6LeMsVQoAAAAAHvsLBdfokM0Yqi7C9gwHmwMMJOw', false);
-- Ejemplo PRE ISTAC: insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.v2.private_key', null, false);
-- Ejemplo PRO ISTAC: insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.v2.private_key', null, false);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.v2.public_key', 'FILL_ME_WITH_PUBLIC_KEY', false);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.v2.public_key', '6LeMsVQoAAAAAGJAf0kujJ2DbJc4XfKT1Pavs9Fh', false);
-- Ejemplo PRE ISTAC: insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.v2.public_key', null, false);
-- Ejemplo PRO ISTAC: insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.v2.public_key', null, false);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.rest.external.base', 'FILL_ME', false);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.rest.external.base', 'https://estadisticas.arte-consultores.com/external-users-external', false);
-- Ejemplo PRE ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.rest.external.base', 'https://www3-pre.gobiernodecanarias.org/aplicaciones/appsistac/external-users', false);
-- Ejemplo PRO ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.rest.external.base', 'https://www3.gobiernodecanarias.org/aplicaciones/appsistac/external-users', false);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.rest.internal.job.send_notice.cron_expression', '0 0 6 * * ? *', false);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.web.internal.url', 'FILL_ME', false);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.web.internal.url', 'https://estadisticas.arte-consultores.com/external-users-internal', false);
-- Ejemplo PRE ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.web.internal.url', 'https://www3-pre.gobiernodecanarias.net/aplicaciones/appsinternalistac/external-users-internal', false);
-- Ejemplo PRO ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.web.internal.url', 'https://www3.gobiernodecanarias.net/aplicaciones/appsinternalistac/external-users-internal', false);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'edatos.shared.allowed_origins', 'FILL_ME', false);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'edatos.shared.allowed_origins', 'https://estadisticas.arte-consultores.com', false);
-- Ejemplo PRE ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'edatos.shared.allowed_origins', 'https://www3-pre.gobiernodecanarias.org', false);
-- Ejemplo PRO ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'edatos.shared.allowed_origins', 'https://www3.gobiernodecanarias.org', false);
-- Ejemplo con varios valores: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'edatos.shared.allowed_origins', 'https://estadisticas.arte-consultores.com,https://www3-pre.gobiernodecanarias.org,https://www3.gobiernodecanarias.org', false);

UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.default.codelist.nationality.urn', 'FILL_ME', true);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.default.codelist.nationality.urn', 'urn:sdmx:org.sdmx.infomodel.codelist.Codelist=ISTAC:CL_M49_724(01.005)', false);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.default.codelist.gender.urn', 'FILL_ME', true);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.default.codelist.gender.urn', 'urn:sdmx:org.sdmx.infomodel.codelist.Codelist=ESTAT:CL_GENDER(2.0)', false);

UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

commit;

-- Ejemplos para el entorno de tests
-----------------------------------------
-- insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE, EXTERNALLY_PUBLISHED) values(1011,1,true,'metamac.edatos_external_users.db.url','jdbc:h2:mem:edatos_external_users;DB_CLOSE_DELAY=-1;INIT=create domain if not exists jsonb as other;MODE=PostgreSQL;', false);
-- UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

-- insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE, EXTERNALLY_PUBLISHED) values(1012,1,true,'metamac.edatos_external_users.db.driver_name','org.h2.Driver', false);
-- UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

-- insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE, EXTERNALLY_PUBLISHED) values(1013,1,true,'metamac.edatos_external_users.db.username',null, false);
-- UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

-- insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE, EXTERNALLY_PUBLISHED) values(1014,1,true,'metamac.edatos_external_users.db.password',null, false);
-- UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

 --insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE, EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'),1,true,'metamac.edatos_external_users.rest.external','https://estadisticas.arte-consultores.com/external-users-external', true);
 --UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

 --insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE, EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'),1,true,'metamac.edatos_external_users.web.external.url','https://estadisticas.arte-consultores.com/external-users-external', true);
 --UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

 --insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE, EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'),1,true,'metamac.portal.rest.external.authentication.captcha.url','https://estadisticas.arte-consultores.com/external-users-external/captcha', true);
 --UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

 --insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE, EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'),1,true,'metamac.portal.rest.external.authentication.captcha.provider','simple', false);
 --UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

 --insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE, EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'),1,true,'metamac.portal.rest.external.authentication.captcha.enable','true', false);
 --UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

 --insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE, EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'),1,true,'metamac.portal.rest.external.authentication.captcha.private_key','FILL_ME_WITH_PRIVATE_KEY', true);
 --UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

 --insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE, EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'),1,true,'metamac.portal.rest.external.authentication.captcha.public_key','6LfaVO0SAAAAAI1cFrnsnO8cNmR9iF_ZCEHuDHnd', true);
 --UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

 --insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE, EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'),1,true,'metamac.portal.rest.external.authentication.captcha.threshold','0.5', false);
 --UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

 --insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE, EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'),1,true,'metamac.edatos_external_users.web.internal.url','https://estadisticas.arte-consultores.com/external-users-internal', true);
 --UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

-- insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE, EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'),1,true,'metamac.portal.web.external.visualizer','https://estadisticas.arte-consultores.com/statistical-visualizer/visualizer', true);
-- UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';


-- commit;
