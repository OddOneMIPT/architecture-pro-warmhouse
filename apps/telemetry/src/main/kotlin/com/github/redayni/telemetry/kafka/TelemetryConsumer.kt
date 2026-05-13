package com.github.redayni.telemetry.kafka

import com.github.redayni.telemetry.repository.TelemetryRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.time.Instant
import java.util.Properties

class TelemetryConsumer(
    private val bootstrapServers: String,
    private val topic: String,
    private val groupId: String,
    private val repository: TelemetryRepository
) {

    private val json = Json { ignoreUnknownKeys = true }

    fun run() {
        val props = Properties().apply {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
            put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
            put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
        }

        KafkaConsumer<String, String>(props).use { consumer ->
            consumer.subscribe(listOf(topic))

            while (true) {
                val records = consumer.poll(Duration.ofSeconds(1))
                for (record in records) {
                    try {
                        val event = json.decodeFromString(
                            TelemetryEvent.serializer(),
                            record.value()
                        )

                        val deviceId = event.deviceId
                        val occurredAt = Instant.parse(event.occurredAt)

                        repository.saveTelemetry(
                            deviceId = deviceId,
                            value = event.value,
                            occurredAt = occurredAt
                        )
                    } catch (ex: Exception) {
                        println("Failed to process telemetry message: ${ex.message}")
                    }
                }
            }
        }
    }
}

@Serializable
data class TelemetryEvent(
    val eventId: String,
    val deviceId: String,
    val value: Double,
    val occurredAt: String
)
