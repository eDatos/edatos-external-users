-- -----------------------------------------------------------------------------------------------
-- EDATOS-4342 - Errores detectados en el proceso de registro y con la cabecera de usuarios
-- -----------------------------------------------------------------------------------------------

-- Añadir propiedad con la url del visualizador externo de operaciones para incluir en los correos
insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.statistical_operations.web.external', 'FILL_ME', true);
-- Ejemplo DESARROLLO: insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.statistical_operations.web.external', 'https://estadisticas.arte-consultores.com/statistical-operations-external', true);
-- Ejemplo PRE ISTAC: insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.statistical_operations.web.external', 'https://www3-pre.gobiernodecanarias.org/aplicaciones/operacionesistac', true);
-- Ejemplo PRO ISTAC: insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.statistical_operations.web.external', 'https://www3.gobiernodecanarias.org/aplicaciones/operacionesistac', true);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

commit;
