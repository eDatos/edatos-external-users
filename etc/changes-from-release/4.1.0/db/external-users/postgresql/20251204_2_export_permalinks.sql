-- -----------------------------------------------------------------------------------------------
-- EDATOS-5349 - Inclusión de targetLinks matomo en eUsuarios
--------------------------------------------------------------------------------------------------

-- Paso 1: Crear la tabla temporal en la base de datos postgresql de METAMAC_PORTAL
CREATE TABLE temp_mig_permalinks
(
    permalink_code VARCHAR(255) NOT NULL UNIQUE,
    operation_code VARCHAR(255) NOT NULL
);

CREATE TABLE temp_mig_permalinks_indicators
(
    permalink_code VARCHAR(255) NOT NULL UNIQUE,
    indicator_code VARCHAR(255) NOT NULL
);

-- Paso 2: Actualiza la tabla temporal con los datos
INSERT INTO temp_mig_permalinks (permalink_code, operation_code)
SELECT permalink.code,
       regexp_replace(
           COALESCE(
               NULLIF(permalink.content::jsonb -> 'queryParams' ->> 'indicatorSystem', ''),
               NULLIF(permalink.content::jsonb -> 'queryParams' ->> 'identifier', ''),
               NULLIF(permalink.content::jsonb -> 'queryParams' ->> 'multidatasetId', '')
           ),
           '(.*:)?(.+?)($|_)(.+)',
           '\2'
       )
FROM tb_permalinks AS permalink
WHERE permalink.content ~ '^\s*[\{\[]'
  AND permalink.content::jsonb -> 'queryParams' ->> 'type' IN ('query', 'dataset', 'indicatorInstance');

INSERT INTO temp_mig_permalinks_indicators (permalink_code, indicator_code)
SELECT permalink.code,
       permalink.content::jsonb -> 'queryParams' ->> 'identifier'
FROM tb_permalinks AS permalink
WHERE permalink.content ~ '^\s*[\{\[]'
  AND permalink.content::jsonb -> 'queryParams' ->> 'type' IN ('indicator');

-- Paso 3: Exportar los datos de la tabla temporal

-- Para la exportación es necesario utilizar DBeaver. Se han de seguir los siguientes pasos:
-- 1. Hacer clic derecho sobre la tabla temporal "temp_mig_permalinks" y seleccionar "Export Data"
-- 2. En la ventana de exportación seleccionar "SQL" como formato de exportación
-- 3. En la ventana de formato asegurarse de que la opción "Omit schema name" está marcada
-- 4. Exportar el resultado a un fichero con el nombre "export_permalinks.sql". El nombre puede ser cualquier otro,
--    pero es el que se usará en los comandos posteriores.
-- 5. Repetir los pasos anteriores para la tabla "temp_mig_permalinks_indicators",
--    exportando el resultado a un fichero con el nombre "export_permalinks_indicators.sql"

-- Paso 4: Eliminar las tablas temporales de la base de datos PORTAL
DROP TABLE temp_mig_permalinks;
DROP TABLE temp_mig_permalinks_indicators;
