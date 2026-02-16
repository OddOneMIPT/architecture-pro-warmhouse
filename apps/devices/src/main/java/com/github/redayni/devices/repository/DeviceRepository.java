package com.github.redayni.devices.repository;

import com.github.redayni.devices.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

    List<Device> findByRoomHouseId(UUID houseId);
}
