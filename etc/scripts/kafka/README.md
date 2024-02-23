# Scripts de Kafka

## Tabla de contenidos

- [Requisitos](#requisitos)
- [Dependencias](#dependencias)
    - [Entorno virtual para las dependencias](#entorno-virtual-para-las-dependencias)
- [Levantar Kafka con Docker](#levantar-kafka-con-docker)
- [Scripts](#scripts)
    - [Productor de Kafka: `kafka_producer.py`](#productor-de-kafka-kafka_producerpy)
    - [Consumidor de Kafka: `kafka_consumer.py`](#consumidor-de-kafka-kafka_consumerpy)
- [Notas finales](#notas-finales)
- [Referencias útiles](#referencias-tiles)

## Requisitos

- Python 3.8

## Dependencias

Instalables a través del archivo `requirements.txt`. Se pueden instalar las dependencias de manera global con el
siguiente comando:

```shell
pip install -r requirements.txt
```

### Entorno virtual para las dependencias

Es recomendable instalar las dependencias en un entorno virtual para evitar conflictos entre las versiones. El entorno
se crea con el siguiente comando:

```shell
python -m venv /dir/to/.venv 
```

Creado el entorno virtual, se debe activar:

```shell
/dir/to/.venv/Scripts/activate
```

Luego se pueden instalar las dependencias normalmente:

```shell
pip install -r requirements.txt
```

## Levantar Kafka con Docker

El fichero docker-compose está ubicado actualmente en https://git.arte-consultores.com/istac/metamac-parent/-/blob/develop/etc/docker/kafka/docker-compose.yml?ref_type=heads.
Se encuentra una copia en local en este repositorio.

Solo hay que levantarlo con docker-compose:

```shell
docker-compose -f docker-compose.yml up
```

## Scripts

### Productor de Kafka: `kafka_producer.py`

El script tiene configurado, por defecto, las siguientes propiedades para conectarse a Kafka:

| Propiedad             | Valor                 | 
| --------------------- | --------------------- |
| --bootstrap-servers   | localhost:9092        |
| --schema-registry     | http://localhost:8081 |

Con respecto al envío de información, los valores por defecto son:

| Propiedad             | Valor                     | 
| --------------------- | ------------------------- |
| --topic               | OPERATION_PUBLICATIONS    |
| --schema-file         | schemas/operation.avsc    |
| --record-key          | Random generated UUID     |
| --record-value        | values/operation_1.json   |

Por lo tanto, solo hay que ejecutarlo para enviar una operación:

```shell
python kafka_producer.py
```

Mientras ara enviar un dataset, hay que pasarle los argumentos correspondientes:

```shell
python kafka_producer.py --topic DATASET_PUBLICATIONS --schema-file schemas/dataset_version.avsc --record-value values/dataset_1.json
```

### Consumidor de Kafka: `kafka_consumer.py`

[Actualmente no en funcionamiento].

## Notas finales

Desafortunadamente, existe un bug en `avro-python3` a partir de la versión 1.9.0 que causa que los enums de los esquemas
Avro no funcionen correctamente en Kafka, lo que causaba problemas con el productor.

El downgrade a la versión 1.8.2 soluciona ese problema, pero eso causa otro con el consumidor de Kafka, al ser la API
de `avro-python3` v1.8.2 incompatible con la de `confluent-kafka` v1.7.0.

La incompatibilidad es bastante sencilla: en `confluent_kafka.avro.load` hay dos funciones: `load` y `loads`. La segunda
llama a una función de `avro-python3` llamada `schema.parse`, pero en la versión 1.8.2 debería ser `schema.Parse`. Para
solucionar este problema hubo intentos de monkey-patching, pero sin éxito.

Hasta que no haya nuevas versiones de `avro-python3` o `confluent-kafka` que solucionen este problema, el consumidor de
Kafka no funciona con enums (y por tanto, no funciona con los esquemas Avro de operaciones y datasets).
