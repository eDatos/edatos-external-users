-- -----------------------------------------------------------------------------------------------
-- EDATOS-5349 - Inclusión de targetLinks matomo en eUsuarios
--------------------------------------------------------------------------------------------------

-- Paso 1: Crear la tabla temporal en la base de datos postgresql de INDICATORS
CREATE TABLE temp_mig_indicators
(
    indicator_code VARCHAR(255) NOT NULL UNIQUE,
    operation_code VARCHAR(255) NOT NULL
);

-- Paso 2: Actualiza la tabla temporal con los datos
INSERT INTO temp_mig_indicators (indicator_code, operation_code)
SELECT code, data_source.source_survey_code
FROM tb_indicators indicator
         JOIN tb_indicators_versions indicator_version ON indicator.id = indicator_version.indicator_fk
         JOIN tb_data_sources data_source ON indicator_version.id = data_source.indicator_version_fk
WHERE indicator_version.proc_status = 'PUBLISHED';

-- Requerido para poder exportar la tabla
GRANT SELECT ON TABLE indicators_bd.temp_mig_indicators TO indicators_bd;

-- Paso 3: Exportar los datos de la tabla temporal a un archivo con pg_dump desde la terminal

-- Powershell
-- ----------
-- Recuerda setear las variables de entorno antes de ejecutar el comando
-- $env:PGPASSWORD="admin"
-- $CLIENT="localhost"
-- pg_dump -h $CLIENT -p 5432 -U indicators_bd -t 'indicators_bd.temp_mig_indicators' --data-only -f temp_mig_indicators.sql indicators_bd

-- Bash
-- ----------
-- Recuerda setear las variables de entorno antes de ejecutar el comando
-- export PGPASSWORD="admin"
-- CLIENT="localhost"
-- pg_dump -h $CLIENT -p 5432 -U indicators_bd -t 'indicators_bd.temp_mig_indicators' --data-only -f temp_mig_indicators.sql indicators_bd

-- Paso 4: Eliminar la tabla temporal en la base de datos postgresql de METAMAC_PORTAL
DROP TABLE temp_mig_indicators;
