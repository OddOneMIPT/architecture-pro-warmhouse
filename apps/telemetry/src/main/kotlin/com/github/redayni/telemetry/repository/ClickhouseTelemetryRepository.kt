package com.github.redayni.telemetry.repository

import com.github.redayni.telemetry.model.CurrentTemperature
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Timestamp
import java.time.Instant
import javax.sql.DataSource

class ClickHouseTelemetryRepository(
    jdbcUrl: String,
    user: String?,
    password: String?
) : TelemetryRepository {

    private val dataSource: DataSource

    init {
        val cfg = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            this.username = user
            this.password = password
            this.driverClassName = "com.clickhouse.jdbc.ClickHouseDriver"
            this.maximumPoolSize = 5
        }
        dataSource = HikariDataSource(cfg)
    }

    override fun saveTelemetry(deviceId: String, value: Double, occurredAt: Instant) {
        dataSource.connection.use { conn ->
            conn.prepareStatement(
                """
                INSERT INTO temperature_timeseries (device_id, timestamp, value, location)
                VALUES (?, ?, ?, ?)
                """.trimIndent()
            ).use { ps ->
                ps.setObject(1, deviceId)
                ps.setTimestamp(2, Timestamp.from(occurredAt))
                ps.setDouble(3, value)
                ps.setString(4, null)
                ps.executeUpdate()
            }
        }
    }

    override fun getLastTemperature(deviceId: String): CurrentTemperature? {
        dataSource.connection.use { conn ->
            conn.prepareStatement(
                """
                SELECT device_id, value, updated_at
                FROM temperature_last_mv
                WHERE device_id = ?
                """.trimIndent()
            ).use { ps ->
                ps.setObject(1, deviceId)
                ps.executeQuery().use { rs ->
                    return if (rs.next()) {
                        val id = rs.getString("device_id")
                        val value = rs.getDouble("value")
                        val updatedAt =
                            rs.getTimestamp("updated_at")?.toInstant() ?: Instant.EPOCH

                        CurrentTemperature(
                            deviceId = id,
                            value = value,
                            updatedAt = updatedAt
                        )
                    } else {
                        null
                    }
                }
            }
        }
    }
}
