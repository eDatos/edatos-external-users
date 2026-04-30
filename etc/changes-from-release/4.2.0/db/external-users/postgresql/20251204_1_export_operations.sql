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

-- Paso 3: Exportar los datos de la tabla temporal

-- Para la exportación es necesario utilizar DBeaver. Se han de seguir los siguientes pasos:
-- 1. Hacer clic derecho sobre la tabla temporal "temp_mig_operations" y seleccionar "Export Data"
-- 2. En la ventana de exportación seleccionar "SQL" como formato de exportación
-- 3. En la ventana de formato asegurarse de que la opción "Omit schema name" está marcada
-- 4. Exportar el resultado a un fichero con el nombre "export_operations.sql". El nombre puede ser cualquier otro,
--    pero es el que se usará en los comandos posteriores.

-- Paso 4: Eliminar la tabla temporal de la base de datos de STATISTICAL-OPERATIONS
DROP TABLE temp_mig_operations;
