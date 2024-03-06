import json
import os
import uuid
from argparse import ArgumentParser

from confluent_kafka.avro import AvroProducer
from rich.console import Console

import avro_loader as loader

script_path = os.path.dirname(__file__)
console = Console(log_time=False)
error_console = Console(log_time=False, stderr=True, style="bold red")


def load_avro_schema_from_file(schema_filepath: str) -> [str, str]:
    key_schema_string = """
    {"type": "string"}
    """

    with console.status(f"Loading Avro schema: {schema_filepath}"):
        key_schema = loader.loads(key_schema_string)
        value_schema = loader.load(schema_filepath)

    console.log(f"Avro schema loaded: '{schema_filepath}'")
    return key_schema, value_schema


def send_record(args):
    key_schema, value_schema = load_avro_schema_from_file(args.schema_file)

    producer_config = {
        "bootstrap.servers": args.bootstrap_servers,
        "schema.registry.url": args.schema_registry
    }

    producer = AvroProducer(producer_config, default_key_schema=key_schema, default_value_schema=value_schema)

    key = args.record_key if args.record_key else str(uuid.uuid4())
    value = args.record_value

    with console.status(f"[yellow]Producing record value to topic '{args.topic}'...[/]"):
        try:
            producer.produce(topic=args.topic, key=key, value=value)
            producer.flush()
        except Exception as e:
            error_console.log(
                f"Exception while producing {key} record value to topic {args.topic}:\n» {e}")
        else:
            console.log(
                f"[bold green]Successfully produced record value to topic '{args.topic}'[/bold green]")


def file_exists(string):
    if os.path.exists(string):
        return string
    else:
        error_console.log(f"'{string}' file wasn't found")
        exit(1)


def json_filepath_or_string(string):
    with console.status("Processing record value..."):
        if os.path.isfile(string):
            console.log(f"Record loaded: '{string}'")
            with open(string, encoding="utf-8") as file:
                value = json.load(file)
        else:
            value = json.loads(string)
        return value


def parse_command_line_args():
    arg_parser = ArgumentParser()

    arg_parser.add_argument("--topic", required=False, help="Topic name, default is OPERATION_PUBLICATIONS",
                            default="OPERATION_PUBLICATIONS")
    arg_parser.add_argument("--bootstrap-servers", required=False, default="localhost:9092",
                            help="Bootstrap server address")
    arg_parser.add_argument("--schema-registry", required=False, default="http://localhost:8081",
                            help="Schema registry url")
    arg_parser.add_argument("--schema-file", required=False, type=file_exists, default="schemas/operation.avsc",
                            help="Avro schema filepath to use, default is 'schemas/operation.avsc'")
    arg_parser.add_argument("--record-key", required=False, type=str,
                            help="Record key. If not provided, will be a random UUID")
    arg_parser.add_argument("--record-value", required=False, type=json_filepath_or_string,
                            default="values/operation_1.json",
                            help="Record value, can be path to a json file or a valid json string. By default is 'values/operation_1.json'")

    return arg_parser.parse_args()


if __name__ == "__main__":
    send_record(parse_command_line_args())
