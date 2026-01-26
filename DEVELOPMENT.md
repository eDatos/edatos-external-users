# Para despliegue en local

Las siguientes propiedades de base de datos deben apuntar al entorno en local:
* 'metamac.edatos_external_users.web.external.url'
* 'metamac.edatos_external_users.rest.external'
* 'metamac.edatos_external_users.rest.external.base'
* 'metamac.edatos_external_users.web.internal.url'


Además, si se va a trabajar a nivel de front se deben tener en cuenta dos aspectos:
* La versión de node máxima soportada es la 12.14.0
* En los ficheros proxy.conf.js se debe comprobar que, en la configuración de proxy, el campo target apunta al entorno correcto
* En los ficheros index.html, rellenar el valor de metadata.apiKey