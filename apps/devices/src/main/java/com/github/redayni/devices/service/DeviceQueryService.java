package com.github.redayni.devices.service;

import com.github.redayni.devices.domain.Device;
import com.github.redayni.devices.domain.House;
import com.github.redayni.devices.repository.DeviceRepository;
import com.github.redayni.devices.repository.HouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DeviceQueryService {

    private final DeviceRepository deviceRepository;
    private final HouseRepository houseRepository;

    public DeviceQueryService(DeviceRepository deviceRepository, HouseRepository houseRepository) {
        this.deviceRepository = deviceRepository;
        this.houseRepository = houseRepository;
    }

    @Transactional(readOnly = true)
    public List<Device> getDevicesByHouse(UUID houseId) {
        return deviceRepository.findByRoomHouseId(houseId);
    }

    @Transactional
    public List<House> getHouses() {
        return houseRepository.findAll();
    }
}