import os
import random
import uuid
import json
from datetime import datetime, timezone

from fastapi import FastAPI, Query
from pydantic import BaseModel
from kafka import KafkaProducer
from typing import Optional

app = FastAPI(title="Temperature API", version="1.0.0")

KAFKA_BOOTSTRAP_SERVERS = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "kafka:9092")
KAFKA_TOPIC = os.getenv("KAFKA_TOPIC", "telemetry.temperature.v1")


class TemperatureResponse(BaseModel):
    sensorId: str
    location: str
    value: float
    unit: str = "C"


def resolve_location_and_sensor(location: str | None, sensor_id: str | None) -> tuple[str, str]:
    location = (location or "").strip()
    sensor_id = (sensor_id or "").strip()

    if not location:
        if sensor_id == "1":
            location = "Living Room"
        elif sensor_id == "2":
            location = "Bedroom"
        elif sensor_id == "3":
            location = "Kitchen"
        else:
            location = "Unknown"

    if not sensor_id:
        if location == "Living Room":
            sensor_id = "1"
        elif location == "Bedroom":
            sensor_id = "2"
        elif location == "Kitchen":
            sensor_id = "3"
        else:
            sensor_id = "0"

    return location, sensor_id


def random_temperature() -> float:
    return round(random.uniform(18.0, 26.0), 2)


def create_producer() -> KafkaProducer:
    return KafkaProducer(
        bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
        value_serializer=lambda v: json.dumps(v).encode("utf-8"),
        key_serializer=lambda v: v.encode("utf-8") if v is not None else None,
        retries=0
    )


producer: Optional[KafkaProducer] = None


def get_producer() -> KafkaProducer:
    global producer
    if producer is None:
        producer = create_producer()
    return producer


def publish_telemetry(sensor_id: str, temperature: float) -> None:
    payload = {
        "eventId": str(uuid.uuid4()),
        "deviceId": sensor_id,
        "value": temperature,
        "occurredAt": datetime.now(timezone.utc).isoformat(),
    }
    try:
        prod = get_producer()
        prod.send(KAFKA_TOPIC, key=sensor_id, value=payload)
    except Exception as e:
        print(f"Failed to publish telemetry to Kafka: {e}")

@app.get("/health")
def health():
    return {"status": "ok"}

@app.get("/temperature", response_model=TemperatureResponse)
def get_temperature(
        location: str | None = Query(default=None, description="Название комнаты"),
        sensor_id: str | None = Query(default=None, description="Идентификатор сенсора"),
):
    resolved_location, resolved_sensor = resolve_location_and_sensor(location, sensor_id)
    temperature = random_temperature()

    publish_telemetry(resolved_sensor, temperature)

    return TemperatureResponse(
        sensorId=resolved_sensor,
        location=resolved_location,
        value=temperature,
        unit="C",
    )


@app.get("/temperature/{sensor_id}", response_model=TemperatureResponse)
def get_temperature(sensor_id: str):
    temperature = random_temperature()

    publish_telemetry(sensor_id, temperature)

    return TemperatureResponse(
        sensorId=sensor_id,
        location="Unknown",
        value=temperature,
        unit="C",
    )