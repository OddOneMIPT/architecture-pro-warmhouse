package com.github.redayni.devices.client.dto;

public record MonolithSensorRequest(
    String name,
    String type,
    String location,
    String unit
) {}