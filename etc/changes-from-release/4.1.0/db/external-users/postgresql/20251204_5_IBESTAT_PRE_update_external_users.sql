-- -----------------------------------------------------------------------------------------------
-- EDATOS-5349 - Inclusión de targetLinks matomo en eUsuarios
--------------------------------------------------------------------------------------------------

-- ESTE SCRIPT SOLO DEBE EJECUTARSE EN IBESTAT *PRE*, EN LA BASE DE DATOS DE EXTERNAL-USERS

DELETE
FROM tb_filters
WHERE external_operation_urn IS NULL
  AND permalink ILIKE '%generarUrlSocial.do%';
