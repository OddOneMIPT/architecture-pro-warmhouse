# Devices Service API

## Prerequisites

- Docker and Docker Compose
- JDK 25+ (for manual run without Docker)
- Gradle (or `./gradlew` wrapper in the project)

## Getting Started

The easiest way to start the service is to use Docker Compose from the project root:

```bash
docker compose up -d devices-postgres devices-service
```

This will:

1. Start a dedicated PostgreSQL instance for the devices service with the `devices` database
2. Apply the schema and seed data from `devices/init.sql`
3. Build and start the `devices-service` container

The API will be available at [http://localhost:8082](http://localhost:8082)

You can also start the entire stack with:

```bash
docker compose up -d
```

## API Testing

A Postman collection is provided for testing the API. Import the `devices-service-api.postman_collection.json` file
into Postman to get started.

## API Endpoints

The service listens on port `8082`, base path `/api/v1`.

Health:

- `GET /actuator/health` – Health check

Main endpoints:

- `GET /api/v1/houses`
  Retrieve all houses known to the devices service.

- `PATCH /api/v1/houses/{houseId}`
  Partially update a house (for example, change its name or address).

- `POST /api/v1/rooms/{roomId}/install-module`
  Install a smart home module into the specified room.
  Based on the module configuration, the service creates devices in that room.
  For heating relay devices it also registers a corresponding sensor/relay in the monolith and stores its ID in the
  `sensor_external_id` field.

- `GET /api/v1/houses/{houseId}/devices`
  Retrieve all devices located in rooms of the given house.

- `PATCH /api/v1/devices/{deviceId}`
  Partially update a device (change name and/or move it to another room).
  For heating relay devices with a non‑null `sensor_external_id`, the service attempts to update the corresponding
  sensor in the monolith.

- `DELETE /api/v1/devices/{deviceId}`
  Delete a device from the system.
  For heating relay devices with a non‑null `sensor_external_id`, the service attempts to delete the corresponding
  sensor in the monolith before removing the device record.
