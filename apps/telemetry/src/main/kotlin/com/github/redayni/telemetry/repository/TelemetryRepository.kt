package com.github.redayni.telemetry.repository

import com.github.redayni.telemetry.model.CurrentTemperature
import java.time.Instant

interface TelemetryRepository {
    fun saveTelemetry(deviceId: String, value: Double, occurredAt: Instant)
    fun getLastTemperature(deviceId: String): CurrentTemperature?
}
