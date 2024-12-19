-- -----------------------------------------------------------------------------------------------
-- EDATOS-4447 - e-Catalogo debe permitir alimentarse desde las tablas publicadas en Alfresco/JAXI

-- Deshabilitar temporalmente las notificaciones por operación al recibir datasets desde el topic de kafka. El motivo es que el reenvío masivo a kafka de datasets para la migración a ckan
-- no envíe las notificaciones.
-- -----------------------------------------------------------------------------------------------


--1) Paso 1 Deshabilitar el job que trata los mensajes que llegan a  la tabla tb_external_datasets 
--Para ello deshabilitar el job que está programado en la propiedad del common-metadata: "metamac.edatos_external_users.rest.internal.job.send_notice.cron_expression"
--1.1) Guardar la configuración actual del job
--1.2) Poner una fecha pasada.
 -- Por ejemplo: '0 56 13 01 JAN ? 2024' En bd common-metadata
 update tb_data_configurations
set conf_value = '0 56 13 01 JAN ? 2024' 
where conf_key = 'metamac.edatos_external_users.rest.internal.job.send_notice.cron_expression';
--1.3) Reiniciar servidor. Con ésto se asegura que no se va a ejecutar.
--1.4) En bd "external_users-bd" Comprobar cual es la última entrada existente en tb_external_datasets. Campo "Recived_date" más reciente. Lo ideal es dejarlo vacío.

--2) Realizar la migración de datos al ckan que se indican en el proyecto de ckan.
  -- Migración datasets de edatos
  -- Migración datasets jaxi
  
--3) Una vez terminada la migración:
--3.1) BD external-users
-- Borrar todas las entradas recibidas durante el proceso de migración de la tabla tb_external_datasets. Posterior al último mensaje que había antes de la migración y visto en paso 1.4
--3.2) BD common-metadata
-- Restaurar el valor del job  cambiado y dejarlo con la configuración original.


  
  