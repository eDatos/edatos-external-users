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

insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.edatos_external_users.db.dialect', 'org.hibernate.dialect.PostgreSQLDialect', false);
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
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.url', 'https://estadisticas.arte-consultores.com/external-users-external/api/captcha', true);
-- Ejemplo PRE ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.url', 'https://www3-pre.gobiernodecanarias.org/aplicaciones/appsistac/external-users/api/captcha', true);
-- Ejemplo PRO ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.portal.rest.external.authentication.captcha.url', 'https://www3.gobiernodecanarias.org/aplicaciones/appsistac/external-users/api/captcha', true);
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

commit;
