package com.github.redayni.devices.web.dto;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;

public record InstallModuleRequest(
    @NotNull UUID moduleId
) {
}