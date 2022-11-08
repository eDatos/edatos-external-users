-- ---------------------------------------------------------------------------------------------------
-- EDATOS-3869 Poder consumir las notificaciones de datos desde un topic de Jaxi
-- Relacionada con : EDATOS-3770 - Se crea entrada en common_metadata que también requiere este parámetro por lo que se pone un if por si la otra tarea ya lo ha puesto.
    -- metamac.kafka.topic.external_datasets_publications
-- ---------------------------------------------------------------------------------------------------

--ATENCIÓN, ESTA PROPIEDAD PUEDE NO EXISTIR EN TODOS LOS ENTORNOS, EN CUYO CASO DEJAR VACÍO.

DO
$do$
DECLARE
    new_key varchar(255);
begin 
	new_key = 'metamac.kafka.topic.external_datasets_publications';
	if (select count(id) from TB_DATA_CONFIGURATIONS where conf_key = new_key)  = 0 then
		 insert INTO TB_DATA_CONFIGURATIONS (ID,VERSION,SYSTEM_PROPERTY,CONF_KEY,CONF_VALUE,EXTERNALLY_PUBLISHED) values(GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'),1,false,new_key,'JAXI_PUBLICATIONS',false);
		 UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';
		 commit;
     else
     	raise notice 'ERROR. El valor ya se encuentra: %', new_key;
	 end if;
end
$do$

