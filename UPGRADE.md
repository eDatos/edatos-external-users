# UPGRADE - Proceso de actualización entre versiones

*Para actualizar de una versión a otra es suficiente con actualizar el WAR a la última versión. El siguiente listado presenta aquellos cambios de versión en los que no es suficiente con actualizar y que requieren por parte del instalador tener más cosas en cuenta. Si el cambio de versión engloba varios cambios de versión del listado, estos han de ejecutarse en orden de más antiguo a más reciente.*

*De esta forma, si tuviéramos una instalación en una versión **A.B.C** y quisieramos actualizar a una versión posterior **X.Y.Z** para la cual existan versiones anteriores que incluyan cambios listados en este documento, se deberá realizar la actualización pasando por todas estas versiones antes de poder llegar a la versión deseada.*

*EJEMPLO: Queremos actualizar desde la versión 1.0.0 a la 3.0.0 y existe un cambio en la base de datos en la actualización de la versión 1.0.0 a la 2.0.0.*

*Se deberá realizar primero la actualización de la versión 1.0.0 a la 2.0.0 y luego desde la 2.0.0 a la 3.0.0*

## 1.1.0 a 1.1.1-SNAPSHOT
* Se añade la propiedad `edatos.shared.allowed_origins`, que hace referencia a las URLs desde las que se puede acceder a la API externa de external-users. Si una url que no está incluida en la propiedad intentara hacer una petición aparecería un error de CORS. Para añadir la propiedad hace falta ejecutar el siguiente script SQL:

```
etc/changes-from-release/1.1.1/db/common-metadata/postgresql/20230516_insert_allowed_origins_configuration.sql
```
