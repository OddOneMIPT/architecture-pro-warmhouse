package com.github.redayni.devices.web.dto;

import com.github.redayni.devices.domain.Device;
import com.github.redayni.devices.domain.House;

public final class DtoMapper {

    private DtoMapper() {
    }

    public static HouseResponse toHouseResponse(House house) {
        return new HouseResponse(
            house.getId(),
            house.getName(),
            house.getAddress(),
            house.getCreatedAt()
        );
    }

    public static DeviceResponse toDeviceResponse(Device device) {
        return new DeviceResponse(
            device.getId(),
            device.getName(),
            device.getRoom().getId(),
            device.getModel().getId(),
            device.getStatus()
        );
    }
}
