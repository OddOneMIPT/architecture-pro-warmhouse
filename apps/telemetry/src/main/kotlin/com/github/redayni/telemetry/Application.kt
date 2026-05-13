package com.github.redayni.telemetry

import com.github.redayni.telemetry.kafka.TelemetryConsumer
import com.github.redayni.telemetry.model.CurrentTemperature
import com.github.redayni.telemetry.repository.ClickHouseTelemetryRepository
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

fun main() {
    val port = System.getenv("TELEMETRY_HTTP_PORT")?.toIntOrNull() ?: 8083

    embeddedServer(Netty, port = port) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    val dbUrl = System.getenv("TELEMETRY_DB_URL") ?: "jdbc:clickhouse://clickhouse:8123/telemetry"
    val dbUser = System.getenv("TELEMETRY_DB_USER") ?: "default"
    val dbPassword = System.getenv("TELEMETRY_DB_PASSWORD") ?: ""

    val repository = ClickHouseTelemetryRepository(
        jdbcUrl = dbUrl,
        user = dbUser,
        password = dbPassword
    )

    val kafkaBootstrap = System.getenv("KAFKA_BOOTSTRAP_SERVERS") ?: "kafka:29092"
    val kafkaTopic = System.getenv("KAFKA_TOPIC") ?: "telemetry.temperature.v1"
    val kafkaGroupId = System.getenv("KAFKA_GROUP_ID") ?: "telemetry-service"

    val consumer = TelemetryConsumer(
        bootstrapServers = kafkaBootstrap,
        topic = kafkaTopic,
        groupId = kafkaGroupId,
        repository = repository
    )

    launch(Dispatchers.IO) {
        consumer.run()
    }

    routing {
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }

        get("/api/v1/devices/{deviceId}/temperature/current") {
            val deviceId = call.parameters["deviceId"]
            if (deviceId.isNullOrBlank()) {
                call.respondText(
                    "deviceId is required",
                    status = HttpStatusCode.BadRequest
                )
                return@get
            }

            val current = repository.getLastTemperature(deviceId)
            if (current == null) {
                call.respond(
                    status = HttpStatusCode.NotFound,
                    message = ErrorResponse(
                        code = "NOT_FOUND",
                        message = "No temperature for device $deviceId"
                    )
                )
            } else {
                call.respond(current.toDto())
            }
        }
    }
}

@Serializable
data class CurrentTemperatureDto(
    val deviceId: String,
    val value: Double,
    val updatedAt: String
)

@Serializable
data class ErrorResponse(
    val code: String,
    val message: String
)

private fun CurrentTemperature.toDto() = CurrentTemperatureDto(
    deviceId = deviceId.toString(),
    value = value,
    updatedAt = updatedAt.toString()
)
