###
# Dockerfile usado para consutruir la imagen de la aplicación external-users-external
#
# Antes de construir la imagen debemos haber compilado previamente el proyecto (desde el directorio raíz):
#
# mvn clean package -Penv
#
# A continuación podremos construir la imagen ejecutando siempre desde el directorio raíz del proyecto:
#
# docker build -f dockerfiles/external-users-external/Dockerfile -t external-users-external:<version> .
#
# Para ejecutar un contenedor asociado a esta imagen:
# docker run -i --rm -p 8080:8080 external-users-external:<version>
#
# Para ejecutar con opciones de debug habilitadas:
# docker run -i --rm \
# -e JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n" \
# -p 8080:8080 \
# -p 5005:5005 \
# external-users-external:<version>
###
FROM tomcat:9.0.98-jdk8
LABEL MAINTAINER=info@arte-consultores.com

RUN mkdir -p /data/edatos/external-users/conf

COPY dockerfiles/external-users-external/conf /data/edatos/external-users/conf
COPY edatos-external-users-external-web/target/*.war /usr/local/tomcat/webapps/external-users-external.war

# Definir variables de entorno con ubicaciones externas de configuración
ENV SPRING_CONFIG_LOCATION=file:/data/edatos/external-users/conf/
ENV DATA_LOCATION_CONFIG=/data/edatos/external-users/conf/data-location.properties

ENV LANG='es_ES.UTF-8'

RUN env

CMD ["catalina.sh", "run"]
