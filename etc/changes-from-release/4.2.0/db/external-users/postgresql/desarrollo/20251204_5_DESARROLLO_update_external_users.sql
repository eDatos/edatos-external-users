-- -----------------------------------------------------------------------------------------------
-- EDATOS-5349 - Inclusión de targetLinks matomo en eUsuarios
--------------------------------------------------------------------------------------------------

-- ESTE SCRIPT SOLO DEBE EJECUTARSE EN DESARROLLO, EN LA BASE DE DATOS DE EXTERNAL-USERS

-- Elimina todos los filtros que sigan teniendo la operación a null dado que hay múltiples filtros
-- que apuntan a permalinks inexistentes.
DELETE
FROM tb_filters
WHERE external_operation_urn IS NULL;

COMMIT;

-- Fecha de última revisión: 2026-04-08
