-- -----------------------------------------------------------------------------------------------
-- EDATOS-3864 - Añadir en el momento del registro más campos que puedan ser útiles para la elaboración de estadísticas
-- -----------------------------------------------------------------------------------------------

-- Añadir propiedad con el valor de la urn de nacionalidades y urn de sexos de la aplicación de e-Usuarios
insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.default.codelist.nationality.urn', 'FILL_ME', true);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.default.codelist.nationality.urn', 'urn:sdmx:org.sdmx.infomodel.codelist.Codelist=ISTAC:CL_M49_724(01.005)', false);
UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.default.codelist.gender.urn', 'FILL_ME', true);
-- Ejemplo DEMO: insert into TB_DATA_CONFIGURATIONS (ID, VERSION, SYSTEM_PROPERTY, CONF_KEY, CONF_VALUE, EXTERNALLY_PUBLISHED) values (GET_NEXT_SEQUENCE_VALUE('DATA_CONFIGURATIONS'), 1, true, 'metamac.default.codelist.gender.urn', 'urn:sdmx:org.sdmx.infomodel.codelist.Codelist=ESTAT:CL_GENDER(2.0)', false);

UPDATE TB_SEQUENCES SET SEQUENCE_NEXT_VALUE = SEQUENCE_NEXT_VALUE + 1 WHERE SEQUENCE_NAME = 'DATA_CONFIGURATIONS';

commit;
