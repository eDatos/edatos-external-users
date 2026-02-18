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

-- Requerido para poder exportar la tabla
GRANT SELECT ON TABLE temp_mig_permalinks TO metamac_portal_bd; -- RECUERDA ACTUALIZAR AL USUARIO CORRESPONDIENTE
GRANT SELECT ON TABLE temp_mig_permalinks_indicators TO metamac_portal_bd; -- RECUERDA ACTUALIZAR AL USUARIO CORRESPONDIENTE

-- Paso 3: Exportar los datos de la tabla temporal a un archivo con pg_dump desde la terminal

-- Powershell
-- ----------
-- Recuerda setear las variables de entorno antes de ejecutar el comando
-- $env:PGPASSWORD="FILL_ME"
-- $CLIENT="FILL_ME" # ejemplo: localhost
-- pg_dump -h $CLIENT -p 5432 -U metamac_portal_bd -t 'temp_mig_permalinks' -t 'temp_mig_permalinks_indicators' --data-only -f temp_mig_permalinks.sql metamac_portal_bd

-- Bash
-- ----------
-- Recuerda setear las variables de entorno antes de ejecutar el comando
-- export PGPASSWORD="FILL_ME"
-- CLIENT="FILL_ME" # ejemplo: localhost
-- pg_dump -h $CLIENT -p 5432 -U metamac_portal_bd -t 'temp_mig_permalinks' -t 'temp_mig_permalinks_indicators' --data-only -f temp_mig_permalinks.sql metamac_portal_bd

-- Paso 4: Eliminar la tabla temporal en la base de datos postgresql de METAMAC_PORTAL
DROP TABLE temp_mig_permalinks;
DROP TABLE temp_mig_permalinks_indicators;
