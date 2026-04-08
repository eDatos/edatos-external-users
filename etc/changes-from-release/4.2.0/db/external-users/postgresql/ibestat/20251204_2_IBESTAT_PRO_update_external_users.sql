-- -----------------------------------------------------------------------------------------------
-- EDATOS-5349 - Inclusión de targetLinks matomo en eUsuarios
--------------------------------------------------------------------------------------------------

-- ESTE SCRIPT SOLO DEBE EJECUTARSE EN IBESTAT *PRO*, EN LA BASE DE DATOS DE EXTERNAL-USERS

UPDATE tb_filters
SET external_operation_urn = 'urn:siemac:org.siemac.metamac.infomodel.statisticaloperations.Operation=000136A'
WHERE permalink = 'https://intranet.caib.es/ibestat-jaxi/generarUrlSocial.do?cod=22666';

UPDATE tb_filters
SET external_operation_urn = 'urn:siemac:org.siemac.metamac.infomodel.statisticaloperations.Operation=000006A"'
WHERE permalink = 'https://intranet.caib.es/ibestat-jaxi/generarUrlSocial.do?cod=22721'; -- kafka offset 67393

UPDATE tb_filters
SET external_operation_urn = 'urn:siemac:org.siemac.metamac.infomodel.statisticaloperations.Operation=000049A'
WHERE permalink = 'https://intranet.caib.es/ibestat-jaxi/generarUrlSocial.do?cod=22483'; -- kafka offset 66475

COMMIT;
