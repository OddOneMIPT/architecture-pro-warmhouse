package com.github.redayni.devices.client.dto;

public record MonolithSensorUpdateRequest(
    String name,
    String type,
    String location,
    Float value,
    String unit,
    String status
) {
}


