-- ---------------------------------------------------------------------------------------------------
-- EDATOS-5872. Incluir api-key en llamadas a la api desde la aplicacion interna de external-users
-- Se añade la propiedad api-key para el consumo de apis desde la aplicacion interna external users
-- "edatos.external_users.rest.internal.api_key"
-- ---------------------------------------------------------------------------------------------------

insert into TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'),1,true,'edatos.external_users.rest.internal.api_key','FILL_ME',false);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

commit;
