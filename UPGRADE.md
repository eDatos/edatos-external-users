# UPGRADE - Proceso de actualización entre versiones

*Para actualizar de una versión a otra es suficiente con actualizar el WAR a la última versión. El siguiente listado presenta aquellos cambios de versión en los que no es suficiente con actualizar y que requieren por parte del instalador tener más cosas en cuenta. Si el cambio de versión engloba varios cambios de versión del listado, estos han de ejecutarse en orden de más antiguo a más reciente.*

*De esta forma, si tuviéramos una instalación en una versión **A.B.C** y quisieramos actualizar a una versión posterior **X.Y.Z** para la cual existan versiones anteriores que incluyan cambios listados en este documento, se deberá realizar la actualización pasando por todas estas versiones antes de poder llegar a la versión deseada.*

*EJEMPLO: Queremos actualizar desde la versión 1.0.0 a la 3.0.0 y existe un cambio en la base de datos en la actualización de la versión 1.0.0 a la 2.0.0.*

*Se deberá realizar primero la actualización de la versión 1.0.0 a la 2.0.0 y luego desde la 2.0.0 a la 3.0.0*

## 2.0.0 a 2.0.1-SNAPSHOT

* Este proyecto añade la cabecera Access-Control-Allow-Origin alimentándola con la propiedad whitelist (véase SecurityConfiguration.java). Es importante que, si la aplicación se sirve tras un frontal de Apache o similar, el mismo no añada cabeceras Access-Control-Allow-Origin: *, pues interactuaría con la añadida por la aplicación. 

## 1.2.1 a 2.0.0

* Se añaden las propiedades `metamac.portal.rest.external.authentication.captcha.v2.private_key` y `metamac.portal.rest.external.authentication.captcha.v2.public_key`, que hacen referencia a la clave secreta y la clave de sitio web de la versión 2 del recaptcha respectivamente. Para añadir las propiedades hace falta ejecutar el siguiente script SQL:

```
etc\changes-from-release\1.2.1\db\common-metadata\postgresql\20231003_insert_recaptcha_v2_keys_configuration.sql
```

* Se actualiza el valor de la propiedad `metamac.portal.rest.external.authentication.captcha.url`. Para actualizarlo
  debe ejecutarse el siguiente script con el valor por entorno de la propiedad:

```
etc\changes-from-release\1.2.1\db\common-metadata\postgresql\20231011_update_captcha_value.sql
```

* Se añade una dependencia en la propiedad `metamac.sie.web.external.url`. Para actualizarlo
  debe ejecutarse el siguiente script localizado en el proyecto `sie`, versión 2.6.1, con el valor 
  por entorno de la propiedad:

```
etc\changes-from-release\2.6.0\db\common-metadata\postgresql\20231009_add_sie_external_web_url.sql
```

## 1.1.0 a 1.2.0
* Se añade la propiedad `edatos.shared.allowed_origins`, que hace referencia a las URLs desde las que se puede acceder a la API externa de external-users. Si una url que no está incluida en la propiedad intentara hacer una petición aparecería un error de CORS. Para añadir la propiedad hace falta ejecutar el siguiente script SQL:

```
etc/changes-from-release/1.1.0/db/common-metadata/postgresql/20230516_insert_allowed_origins_configuration.sql
```

* Se añade la propiedad `metamac.portal.rest.external.authentication.captcha.threshold`, que hace referencia al número límite que diferencia a los bots (números menores al de la propiedad) de las personas (números mayores o iguales al de la propiedad) si el captcha en uso es el recaptcha. Para añadir la propiedad hace falta ejecutar el siguiente script SQL:

```
etc/changes-from-release/1.1.0/db/common-metadata/postgresql/20230601_insert_recaptcha_threshold_configuration.sql
```

* Se añaden las propiedades `metamac.default.codelist.nationality.urn` y `metamac.default.codelist.gender.urn`, que hace referencia al URN para obtener los datos de las nacionalidades y géneros de la API externa de stadistical-resources. Para añadir las propiedades hace falta ejecutar el siguiente script SQL:

```
etc/changes-from-release/1.1.0/db/common-metadata/postgresql/20230602_insert_urn_nationalities_and_genders_configuration.sql
```
