# UPGRADE - Proceso de actualización entre versiones

*Para actualizar de una versión a otra es suficiente con actualizar el WAR a la última versión. El siguiente listado presenta aquellos cambios de versión en los que no es suficiente con actualizar y que requieren por parte del instalador tener más cosas en cuenta. Si el cambio de versión engloba varios cambios de versión del listado, estos han de ejecutarse en orden de más antiguo a más reciente.*

*De esta forma, si tuviéramos una instalación en una versión **A.B.C** y quisieramos actualizar a una versión posterior **X.Y.Z** para la cual existan versiones anteriores que incluyan cambios listados en este documento, se deberá realizar la actualización pasando por todas estas versiones antes de poder llegar a la versión deseada.*

*EJEMPLO: Queremos actualizar desde la versión 1.0.0 a la 3.0.0 y existe un cambio en la base de datos en la actualización de la versión 1.0.0 a la 2.0.0.*

*Se deberá realizar primero la actualización de la versión 1.0.0 a la 2.0.0 y luego desde la 2.0.0 a la 3.0.0*

## 3.0.0 a 3.5.1-SNAPSHOT

* Se añaden las propiedades `metamac.edatos_external_users.simplifiedSignUp` que permite habilitar y deshabilitar el formulario
de registro simplificado. Para añadir la propiedad hace falta ejecutar el siguiente script SQL:

```
etc\changes-from-release\3.5.1\db\common-metadata\postgresql\20250314_insert_simplified_sign_up_configuration.sql
```

## 2.3.1 a 3.0.0

### Breaking changes

A partir de esta versión se cambia la manera en la que las aplicaciones acceden al captcha. Por lo tanto, si se sube a esta versión las aplicaciones que usen el captcha deberán estar actualizadas para adaptarse a este cambio. 
Actualmente dichas aplicaciones son portal e indicadores. En el UPGRADE.md de dichos proyectos estará específicada la versión en la que se adaptaron a estos cambios.

Los cambios en cuestión es añadir los métodos del captcha bajo el objeto global `Edatos.captcha`:

- `showCaptcha()` pasa a ser `Edatos.captcha.showCaptcha()`.
- `showCaptchaWithButton()` pasa a ser `Edatos.captcha.showCaptchaWithButton()`.
- `requestWithCaptcha()` pasa a ser `Edatos.captcha.requestWithCaptcha()`.

Para más información, consultar la documentación del uso del captcha en el confluence.

## 2.1.1 a 2.2.0

* Se añaden las propiedades `metamac.default.codelist.gender.restriction` y `metamac.default.codelist.nationality.restriction` que hacen referencia a las **restricciones** utilizadas en las peticiones a la API de SRM para conseguir los códigos de los sexos y los paises respectivamente. Para añadir las propiedades hace falta ejecutar el siguiente script SQL:

```
etc\changes-from-release\2.1.1\db\common-metadata\postgresql\20240216_insert_paises_and_sexo_restriction_configuration.sql
```

## 2.0.0 a 2.1.0

* Este proyecto añade la cabecera Access-Control-Allow-Origin alimentándola con la propiedad whitelist (véase SecurityConfiguration.java). Es importante que, si la aplicación se sirve tras un frontal de Apache o similar, el mismo no añada cabeceras Access-Control-Allow-Origin: *, pues interactuaría con la añadida por la aplicación.
* Se añade la propiedad `metamac.statistical_operations.web.external` que hacen referencia al visualizador externo de operaciones. Para añadir la propiedad hace falta ejecutar el siguiente script SQL:

```
etc\changes-from-release\2.0.1\db\common-metadata\postgresql\20240216_insert_statistical_operations_external_web_configuration.sql
```

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

* Se añaden las propiedades `metamac.default.codelist.nationality.urn` y `metamac.default.codelist.gender.urn`, que hace referencia al URN para obtener los datos de las nacionalidades y sexos de la API externa de stadistical-resources. Para añadir las propiedades hace falta ejecutar el siguiente script SQL:

```
etc/changes-from-release/1.1.0/db/common-metadata/postgresql/20230602_insert_urn_nationalities_and_genders_configuration.sql
```
