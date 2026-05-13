package com.github.redayni.telemetry.model

import java.time.Instant

data class CurrentTemperature(
    val deviceId: String,
    val value: Double,
    val updatedAt: Instant
)
