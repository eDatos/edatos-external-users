-- -----------------------------------------------------------------------------------------------
-- EDATOS-3702 - Implementar whitelist para las peticiones CORS
-- -----------------------------------------------------------------------------------------------

-- Añadir propiedad con los dominios permitidos para hacer peticiones CORS
insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'edatos.shared.allowed_origins', 'FILL_ME', false);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'edatos.shared.allowed_origins', 'https://estadisticas.arte-consultores.com', false);
-- Ejemplo PRE ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'edatos.shared.allowed_origins', 'https://www3-pre.gobiernodecanarias.org', false);
-- Ejemplo PRO ISTAC: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'edatos.shared.allowed_origins', 'https://www3.gobiernodecanarias.org', false);
-- Ejemplo con varios valores: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'edatos.shared.allowed_origins', 'https://estadisticas.arte-consultores.com,https://www3-pre.gobiernodecanarias.org,https://www3.gobiernodecanarias.org', false);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

commit;
