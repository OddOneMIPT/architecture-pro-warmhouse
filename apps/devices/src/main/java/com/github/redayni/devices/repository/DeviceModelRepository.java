package com.github.redayni.devices.repository;

import com.github.redayni.devices.domain.DeviceModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeviceModelRepository extends JpaRepository<DeviceModel, UUID> {
}
