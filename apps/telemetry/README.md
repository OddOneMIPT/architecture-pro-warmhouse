Вот вариант README для telemetry-service в том же стиле:

````markdown
# Telemetry Service API

## Prerequisites

- Docker and Docker Compose
- JDK 25+ (for manual run without Docker)
- Gradle (or `./gradlew` wrapper in the project)

## Getting Started

The easiest way to start the telemetry service is to use Docker Compose from the project root:

```bash
docker compose up -d telemetry-clickhouse kafka telemetry-service
```
````

This will:

1. Start a ClickHouse instance for telemetry with the `telemetry` database
2. Apply the schema from `telemetry/init.sql` (tables `temperature_timeseries` and `temperature_last` + materialized
   view)
3. Start a Kafka broker
4. Build and start the `telemetry-service` container

The API will be available at [http://localhost:8083](http://localhost:8083).

You can also start the entire stack with:

```bash
docker compose up -d
```

In this configuration:

- `temperature-api` publishes random temperature readings to Kafka
- `telemetry-service` consumes these messages and stores them in ClickHouse
- You can query the latest temperature for a device through the HTTP API

## API Testing

A Postman collection is provided for testing the API. Import the `telemetry-service-api.postman_collection.json` file
into Postman to get started.

## API Endpoints

The service listens on port `8083`.

Health:

- `GET /health` – Health check endpoint, returns a simple JSON status.

Main endpoint:

- `GET /api/v1/devices/{deviceId}/temperature/current`
  Returns the latest known temperature for the given device.
