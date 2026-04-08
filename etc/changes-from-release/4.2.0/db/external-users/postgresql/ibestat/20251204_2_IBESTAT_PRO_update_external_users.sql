-- -----------------------------------------------------------------------------------------------
-- EDATOS-5349 - Inclusión de targetLinks matomo en eUsuarios
--------------------------------------------------------------------------------------------------

-- ESTE SCRIPT SOLO DEBE EJECUTARSE EN IBESTAT *PRO*, EN LA BASE DE DATOS DE EXTERNAL-USERS

UPDATE tb_filters
SET external_operation_urn = 'urn:siemac:org.siemac.metamac.infomodel.statisticaloperations.Operation=000136A'
WHERE permalink = 'https://intranet.caib.es/ibestat-jaxi/generarUrlSocial.do?cod=22666'; -- kafka offset 97,000, dataset U302002_1001

UPDATE tb_filters
SET external_operation_urn = 'urn:siemac:org.siemac.metamac.infomodel.statisticaloperations.Operation=000006A"'
WHERE permalink = 'https://intranet.caib.es/ibestat-jaxi/generarUrlSocial.do?cod=22721'; -- kafka offset 67393, dataset def_70200

UPDATE tb_filters
SET external_operation_urn = 'urn:siemac:org.siemac.metamac.infomodel.statisticaloperations.Operation=000186A'
WHERE permalink = 'https://intranet.caib.es/ibestat-jaxi/generarUrlSocial.do?cod=22925'; -- kafka offset 96821, dataset I3061_evo3

COMMIT;

-- ¿Cómo se han averiguado las operaciones de estos recursos?
-- 1. Se acccede al enlace del permalink, que lleva al visualizador Jaxi, en donde se ve el nombre la operación a la que corresponde el recurso.
-- 2. Se busca la operación en el catálogo de operaciones de eUsuarios, y se obtiene su URN, que es la que se asigna al filtro.
-- 3. Luego, el nombre del dataset se puede buscar en eTerritorios, que devuelve el enlace del visualizador
--    donde se muestra el código del dataset Jaxi (p. ej.: pxName=I3061_evo3.px)
-- 4. El dataset se puede buscar en el topic de JAXI_PUBLICATIONS, y se puede buscar el offset más reciente para comprobar
--    que efectivamente está adscrito a la operación.
-- 5. También se puede buscar directamente el nombre del dataset que se muestra en el visualizador en el topic de JAXI_PUBLICATIONS.
