package com.github.redayni.devices.web.dto;

import java.util.UUID;

public record UpdateDeviceRequest(
    String name,
    UUID roomId
) {}