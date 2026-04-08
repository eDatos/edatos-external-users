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
SELECT DISTINCT indicator.code, data_source.source_survey_code
FROM tb_indicators indicator
         JOIN tb_indicators_versions indicator_version ON indicator.id = indicator_version.indicator_fk
         JOIN tb_data_sources data_source ON indicator_version.id = data_source.indicator_version_fk
WHERE indicator_version.proc_status = 'PUBLISHED';

-- Paso 3: Exportar los datos de la tabla temporal

-- Para la exportación es necesario utilizar DBeaver. Se han de seguir los siguientes pasos:
-- 1. Hacer clic derecho sobre la tabla temporal "temp_mig_indicators" y seleccionar "Export Data"
-- 2. En la ventana de exportación seleccionar "SQL" como formato de exportación
-- 3. En la ventana de formato asegurarse de que la opción "Omit schema name" está marcada
-- 4. Exportar el resultado a un fichero con el nombre "export_indicators.sql". El nombre puede ser cualquier otro,
--    pero es el que se usará en los comandos posteriores.

-- Paso 4: Eliminar la tabla temporal de la base de datos de INDICATORS
DROP TABLE temp_mig_indicators;
