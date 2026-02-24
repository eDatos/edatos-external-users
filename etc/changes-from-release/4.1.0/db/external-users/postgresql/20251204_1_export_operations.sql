-- -----------------------------------------------------------------------------------------------
-- EDATOS-5349 - Inclusión de targetLinks matomo en eUsuarios
--------------------------------------------------------------------------------------------------

-- Paso 1: Crear la tabla temporal en la base de datos postgresql de STATISTICAL-OPERATIONS
CREATE TABLE temp_mig_operations
(
    code                  varchar(255)  NOT NULL UNIQUE,
    name                  jsonb         NOT NULL,
    urn                   varchar(4000) NOT NULL UNIQUE,
    external_category_urn varchar(4000) NOT NULL,
    publication_date      timestamp     NOT NULL
);

-- Paso 2: Actualiza la tabla temporal con los datos
INSERT INTO temp_mig_operations (urn, code, external_category_urn, publication_date, name)
SELECT operations.urn,
       operations.code,
       sub_area.urn AS external_category_urn,
       operations.inventory_date,
       jsonb_build_object(
           'texts',
           coalesce(
               jsonb_agg(
                   jsonb_build_object('label', title_loc_str.label, 'locale', title_loc_str.locale)
               ), '[]'::jsonb
           )
       ) AS name
FROM tb_operations operations
         JOIN tb_external_items sub_area ON operations.subject_area_fk = sub_area.id
         JOIN tb_international_strings title_str ON operations.title_fk = title_str.id
         JOIN tb_localised_strings title_loc_str ON title_str.id = title_loc_str.international_string_fk
WHERE proc_status = 'PUBLISH_EXTERNALLY'
GROUP BY operations.urn, operations.code, operations.inventory_date, sub_area.urn;

-- Requerido para poder exportar la tabla
GRANT SELECT ON TABLE temp_mig_operations TO metamac_statistical_operations_bd; -- RECUERDA ACTUALIZAR AL USUARIO CORRESPONDIENTE

-- Paso 3: Exportar los datos de la tabla temporal a un archivo con pg_dump desde la terminal

-- Powershell
-- ----------
-- Recuerda setear las variables de entorno antes de ejecutar el comando
-- $env:PGPASSWORD="FILL_ME"
-- $CLIENT="FILL_ME" # ejemplo: localhost
-- pg_dump -h $CLIENT -p 5432 -U metamac_statistical_operations_bd -t 'temp_mig_operations' --data-only -f temp_mig_operations.sql metamac_statistical_operations_bd

-- Bash
-- ----------
-- Recuerda setear las variables de entorno antes de ejecutar el comando
-- export PGPASSWORD="FILL_ME"
-- CLIENT="FILL_ME" # ejemplo: localhost
-- pg_dump -h $CLIENT -p 5432 -U metamac_statistical_operations_bd -t 'temp_mig_operations' --data-only -f temp_mig_operations.sql metamac_statistical_operations_bd

-- Paso 4: Eliminar la tabla temporal en la base de datos postgresql de STATISTICAL-OPERATIONS
DROP TABLE temp_mig_operations;
