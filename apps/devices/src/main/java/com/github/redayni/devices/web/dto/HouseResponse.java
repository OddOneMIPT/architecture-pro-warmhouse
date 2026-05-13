package com.github.redayni.devices.web.dto;

import java.time.Instant;
import java.util.UUID;

public record HouseResponse(UUID id, String name, String address, Instant createdAt) {
}
