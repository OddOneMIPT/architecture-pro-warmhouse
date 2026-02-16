# Temperature API

A small emulator service that returns a random temperature on each request and publishes a telemetry event to Kafka.

## Prerequisites

- Docker and Docker Compose
- Python 3.14+ (for manual run without Docker)

## Getting Started

The easiest way to start the service is to use Docker Compose from the project root:

```bash
docker compose up -d temperature-api
```

This will:

1. Start the Kafka broker (if it is not already running)
2. Build and start the `temperature-api` container
3. Expose the API on port `8081`

The API will be available at [http://localhost:8081](http://localhost:8081)

You can also start the entire stack with:

```bash
docker compose up -d
```

## API Endpoints

The service listens on port `8081`.

- `GET /health`
  Simple health check endpoint.

- `GET /temperature`
  Returns a random temperature for a given room or sensor.
  Query parameters:
  - `location` – room name (optional)
  - `sensor_id` – sensor identifier (optional)

  If some parameters are missing, they are derived using the following rules:
  - If `location` is missing:
    - `sensor_id="1"` → `location="Living Room"`
    - `sensor_id="2"` → `location="Bedroom"`
    - `sensor_id="3"` → `location="Kitchen"`
    - any other `sensor_id` → `location="Unknown"`

  - If `sensor_id` is missing:
    - `location="Living Room"` → `sensor_id="1"`
    - `location="Bedroom"` → `sensor_id="2"`
    - `location="Kitchen"` → `sensor_id="3"`
    - any other `location` → `sensor_id="0"`

  On each call the service also publishes a telemetry event to Kafka in topic `telemetry.temperature.v1` with payload:

  ```json
  {
    "eventId": "<uuid>",
    "deviceId": "<sensorId>",
    "value": <float>,
    "occurredAt": "<timestamp>"
  }
  ```

- `GET /temperature/{sensor_id}`
  Returns a random temperature for the given sensor ID.
  Response fields are the same as for `/temperature`; `location` is set to `"Unknown"` for sensor IDs that do not
  match a known mapping.

  On each call the service also publishes a telemetry event to Kafka in topic `telemetry.temperature.v1` with payload:

  ```json
  {
    "eventId": "<uuid>",
    "deviceId": "<sensorId>",
    "value": <float>,
    "occurredAt": "<timestamp>"
  }
  ```
