-- -----------------------------------------------------------------------------------------------
-- EDATOS-5349 - Inclusión de targetLinks matomo en eUsuarios
--------------------------------------------------------------------------------------------------

-- Paso 1: Crear la tabla temporal en la base de datos postgresql de METAMAC_PORTAL
CREATE TABLE temp_mig_permalinks
(
    permalink_code VARCHAR(255) NOT NULL UNIQUE,
    code           VARCHAR(255) NOT NULL
);

-- Paso 2: Actualiza la tabla temporal con los datos
INSERT INTO temp_mig_permalinks (permalink_code, code)
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
WHERE permalink.content::jsonb -> 'queryParams' ->> 'type' IN ('query', 'dataset', 'indicatorInstance');

GRANT SELECT ON TABLE metamac_portal_bd.temp_mig_permalinks TO metamac_portal_bd;

-- Paso 3: Exportar los datos de la tabla temporal a un archivo con pg_dump desde la terminal

-- Powershell
-- ----------
-- Recuerda setear las variables de entorno antes de ejecutar el comando
-- $env:PGPASSWORD="admin"
-- $CLIENT="localhost"
-- pg_dump -h $CLIENT -p 5432 -U metamac_portal_bd -t 'metamac_portal_bd.temp_mig_permalinks' --data-only -f temp_mig_permalinks.sql metamac_portal_bd

-- Bash
-- ----------
-- Recuerda setear las variables de entorno antes de ejecutar el comando
-- export PGPASSWORD="admin"
-- CLIENT="localhost"
-- pg_dump -h $CLIENT -p 5432 -U metamac_portal_bd -t 'metamac_portal_bd.temp_mig_permalinks' --data-only -f temp_mig_permalinks.sql metamac_portal_bd

-- Paso 4: Eliminar la tabla temporal en la base de datos postgresql de METAMAC_PORTAL
DROP TABLE temp_mig_permalinks;
