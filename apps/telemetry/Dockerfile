# --- stage 1: build ---
FROM gradle:9.2.1-jdk21 AS build

WORKDIR /workspace
COPY . .
RUN gradle installDist --no-daemon

# --- stage 2: run ---
FROM eclipse-temurin:21-jre

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /workspace/build/install/telemetry-service ./telemetry-service

EXPOSE 8083

ENTRYPOINT ["./telemetry-service/bin/telemetry-service"]
