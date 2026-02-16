package com.github.redayni.devices.client.dto;

import java.time.Instant;

public record MonolithSensorResponse(
    Integer id,
    String name,
    String type,
    String location,
    Float value,
    String unit,
    String status,
    Instant lastUpdated,
    Instant createdAt
) {}
