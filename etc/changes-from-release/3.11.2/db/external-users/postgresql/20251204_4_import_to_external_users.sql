-- -----------------------------------------------------------------------------------------------
-- EDATOS-5349 - Inclusión de targetLinks matomo en eUsuarios
--------------------------------------------------------------------------------------------------

-- Paso 1: Crear la tabla temporal en la base de datos postgresql de EXTERNAL-USERS
CREATE TABLE temp_mig_operations
(
    code                  varchar(255)  NOT NULL UNIQUE,
    name                  jsonb         NOT NULL,
    urn                   varchar(4000) NOT NULL UNIQUE,
    external_category_urn varchar(4000) NOT NULL,
    publication_date      timestamp     NOT NULL
);

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

CREATE TABLE temp_mig_indicators
(
    indicator_code VARCHAR(255) NOT NULL UNIQUE,
    operation_code VARCHAR(255) NOT NULL
);

-- Paso 2: Actualiza el esquema de las tablas temporales

-- Powershell
-- ----------
-- (Get-Content temp_mig_operations.sql) -replace 'COPY metamac_statistical_operations_bd', 'COPY "external_users-bd"' | Set-Content temp_mig_operations.sql
-- (Get-Content temp_mig_permalinks.sql) -replace 'COPY metamac_portal_bd', 'COPY "external_users-bd"' | Set-Content temp_mig_permalinks.sql
-- (Get-Content temp_mig_indicators.sql) -replace 'COPY indicators_bd', 'COPY "external_users-bd"' | Set-Content temp_mig_indicators.sql

-- Bash
-- ----------
-- sed -i 's/COPY metamac_statistical_operations_bd\./COPY "external_users-bd"/g' temp_mig_operations.sql
-- sed -i 's/COPY metamac_portal_bd\./COPY "external_users-bd"/g' temp_mig_permalinks.sql
-- sed -i 's/COPY indicators_bd\./COPY "external_users-bd"/g' temp_mig_indicators.sql

-- Paso 3: Importar los datos generados en EXTERNAL-USERS

-- Powershell
-- ----------
-- Recuerda setear las variables de entorno antes de ejecutar el comando
-- $env:PGPASSWORD="FILL_ME"
-- $CLIENT="FILL_ME" # ejemplo: localhost
-- psql -h $CLIENT -p 5432 -U external_users-bd -d external_users-bd -f temp_mig_operations.sql
-- psql -h $CLIENT -p 5432 -U external_users-bd -d external_users-bd -f temp_mig_permalinks.sql
-- psql -h $CLIENT -p 5432 -U external_users-bd -d external_users-bd -f temp_mig_indicators.sql

-- Bash
-- ----------
-- Recuerda setear las variables de entorno antes de ejecutar el comando
-- export PGPASSWORD="FILL_ME"
-- CLIENT="FILL_ME" # ejemplo: localhost
-- psql -h $CLIENT -p 5432 -U external_users-bd -d external_users-bd -f temp_mig_operations.sql
-- psql -h $CLIENT -p 5432 -U external_users-bd -d external_users-bd -f temp_mig_permalinks.sql
-- psql -h $CLIENT -p 5432 -U external_users-bd -d external_users-bd -f temp_mig_indicators.sql

-- Paso 4: Insertar los datos de las operaciones desde la tabla temporal a las tablas de EXTERNAL-USERS

INSERT INTO tb_external_items (id, opt_lock, code, name, type, urn)
SELECT nextval('seq_tb_external_items'), 0, operation.code, operation.name, 'statisticalOperations#operation', operation.urn
FROM temp_mig_operations operation
ON CONFLICT DO NOTHING;

INSERT INTO tb_external_operations (external_category_urn, enabled, notifications_enabled, publication_date, external_item_fk)
SELECT operation.external_category_urn, FALSE, TRUE, operation.publication_date, external_item.id
FROM temp_mig_operations operation
         JOIN tb_external_items external_item ON operation.urn = external_item.urn
ON CONFLICT DO NOTHING;

-- Paso 5: Asignación de las operaciones a los filtros que no lo tengan todavía

-- Primero con los recursos estadísticos que tienen la operación directamente extraíble de su permalink
UPDATE tb_filters filter
SET external_operation_urn = (SELECT operation_ext_item.urn
                              FROM tb_external_items operation_ext_item
                                       JOIN temp_mig_permalinks permalink ON permalink.permalink_code = filter.permalink
                              WHERE operation_ext_item.code = permalink.operation_code)
WHERE filter.external_operation_urn IS NULL;

-- Segundo con los indicadores
UPDATE tb_filters filter
SET external_operation_urn = (SELECT operation_ext_item.urn
                              FROM tb_external_items operation_ext_item
                                       JOIN temp_mig_permalinks_indicators permalink ON permalink.permalink_code = filter.permalink
                                       JOIN temp_mig_indicators indicator ON indicator.indicator_code = permalink.indicator_code
                              WHERE operation_ext_item.code = indicator.operation_code)
WHERE filter.external_operation_urn IS NULL;

-- Paso 6: Eliminar la tabla temporal en la base de datos postgresql de EXTERNAL-USERS
DROP TABLE temp_mig_operations;
DROP TABLE temp_mig_permalinks;
DROP TABLE temp_mig_permalinks_indicators;
DROP TABLE temp_mig_indicators;
