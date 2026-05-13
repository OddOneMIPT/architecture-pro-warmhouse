# --- stage 1: build ---
FROM gradle:9.2.1-jdk25 AS build

WORKDIR /workspace
COPY . .
RUN gradle bootJar --no-daemon

# --- stage 2: run ---
FROM eclipse-temurin:25-jre

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /workspace/build/libs/devices-service-0.0.1.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/app.jar"]
