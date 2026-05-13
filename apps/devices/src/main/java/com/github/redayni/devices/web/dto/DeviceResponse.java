package com.github.redayni.devices.web.dto;

import java.util.UUID;

public record DeviceResponse(UUID id, String name, UUID roomId, UUID modelId, String status) {
}
