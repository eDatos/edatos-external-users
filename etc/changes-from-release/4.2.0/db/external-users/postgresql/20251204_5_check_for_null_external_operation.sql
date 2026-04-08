-- -----------------------------------------------------------------------------------------------
-- EDATOS-5349 - Inclusión de targetLinks matomo en eUsuarios
--------------------------------------------------------------------------------------------------

-- IMPORTANTE: Ejecutar primero los scripts por entorno antes de ejecutar este script.
-- Este script verificará que no existen filtros con external_operation_urn NULL en tb_filters.
-- Si existen, el script fallará y mostrará cuántos filtros tienen este problema.

DO $$
DECLARE
    null_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO null_count
    FROM tb_filters
    WHERE external_operation_urn IS NULL;

    IF null_count > 0 THEN
        RAISE EXCEPTION 'Existen % filtros con external_operation_urn NULL en tb_filters. Todos los filtros deben tener una operación externa asignada.', null_count;
    END IF;
END $$;
