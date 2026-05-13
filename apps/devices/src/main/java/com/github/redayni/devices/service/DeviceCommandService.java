package com.github.redayni.devices.service;

import com.github.redayni.devices.client.MonolithClient;
import com.github.redayni.devices.client.dto.MonolithSensorRequest;
import com.github.redayni.devices.client.dto.MonolithSensorResponse;
import com.github.redayni.devices.client.dto.MonolithSensorUpdateRequest;
import com.github.redayni.devices.domain.Device;
import com.github.redayni.devices.domain.DeviceModel;
import com.github.redayni.devices.domain.House;
import com.github.redayni.devices.domain.Module;
import com.github.redayni.devices.domain.ModuleDeviceModel;
import com.github.redayni.devices.domain.Room;
import com.github.redayni.devices.repository.DeviceModelRepository;
import com.github.redayni.devices.repository.DeviceRepository;
import com.github.redayni.devices.repository.HouseRepository;
import com.github.redayni.devices.repository.ModuleDeviceModelRepository;
import com.github.redayni.devices.repository.ModuleRepository;
import com.github.redayni.devices.repository.RoomRepository;
import com.github.redayni.devices.web.dto.UpdateDeviceRequest;
import com.github.redayni.devices.web.dto.UpdateHouseRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DeviceCommandService {

    private static final String STATUS_ACTIVE = "active";

    private static final String HEATING_TYPE_NAME = "HEATING_RELAY";
    private static final String HEATING_TYPE = "heating relay";
    private static final String UNIT_CELSIUS = "C";

    private static final Logger log = LoggerFactory.getLogger(DeviceCommandService.class);

    private final HouseRepository houseRepository;
    private final RoomRepository roomRepository;
    private final ModuleRepository moduleRepository;
    private final ModuleDeviceModelRepository moduleDeviceModelRepository;
    private final DeviceModelRepository deviceModelRepository;
    private final DeviceRepository deviceRepository;
    private final MonolithClient monolithClient;

    public DeviceCommandService(
        HouseRepository houseRepository,
        RoomRepository roomRepository,
        ModuleRepository moduleRepository,
        ModuleDeviceModelRepository moduleDeviceModelRepository,
        DeviceModelRepository deviceModelRepository,
        DeviceRepository deviceRepository,
        MonolithClient monolithClient
    ) {
        this.houseRepository = houseRepository;
        this.roomRepository = roomRepository;
        this.moduleRepository = moduleRepository;
        this.moduleDeviceModelRepository = moduleDeviceModelRepository;
        this.deviceModelRepository = deviceModelRepository;
        this.deviceRepository = deviceRepository;
        this.monolithClient = monolithClient;
    }

    @Transactional
    public List<Device> installModuleInRoom(UUID roomId, UUID moduleId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + roomId));

        Module module = moduleRepository.findById(moduleId)
            .orElseThrow(() -> new ResourceNotFoundException("Module not found: " + moduleId));

        List<ModuleDeviceModel> moduleModels = moduleDeviceModelRepository.findByModuleId(module.getId());
        if (moduleModels.isEmpty()) {
            throw new ResourceNotFoundException("Module has no device configuration: " + moduleId);
        }

        List<Device> createdDevices = new ArrayList<>();
        for (ModuleDeviceModel mdm : moduleModels) {
            DeviceModel model = deviceModelRepository.findById(mdm.getDeviceModel().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Device model not found: " + mdm.getDeviceModel().getId()));

            for (int i = 0; i < mdm.getQuantity(); i++) {
                String deviceName = model.getName() + " (" + room.getName() + ")";
                Device device = new Device(room, model, deviceName, STATUS_ACTIVE);

                if (isHeatingRelay(model)) {

                    int externalId = registerHeatingDeviceInMonolith(deviceName, room.getName());
                    device.setSensorExternalId(externalId);
                }

                createdDevices.add(device);
            }
        }
        return deviceRepository.saveAll(createdDevices);
    }

    private boolean isHeatingRelay(DeviceModel model) {
        return model.getType() != null
            && HEATING_TYPE_NAME.equalsIgnoreCase(model.getType().getName());
    }

    private int registerHeatingDeviceInMonolith(String deviceName, String roomName) {
        MonolithSensorRequest request = new MonolithSensorRequest(
            deviceName,
            HEATING_TYPE,
            roomName,
            UNIT_CELSIUS
        );

        MonolithSensorResponse response = monolithClient.createSensor(request);
        return response.id();
    }

    @Transactional
    public void deleteDevice(UUID deviceId) {
        Device device = deviceRepository.findById(deviceId)
            .orElseThrow(() -> new ResourceNotFoundException("Device not found: " + deviceId));

        if (device.getSensorExternalId() != null) {
            try {
                monolithClient.deleteSensor(device.getSensorExternalId());
            } catch (Exception ex) {
                log.warn("Failed to delete sensor {} from monolith: {}", device.getSensorExternalId(), ex.getMessage());
            }
        }

        deviceRepository.delete(device);
    }

    @Transactional
    public House updateHouse(UUID houseId, UpdateHouseRequest request) {
        House house = houseRepository.findById(houseId)
            .orElseThrow(() -> new ResourceNotFoundException("House not found: " + houseId));

        if (request.name() != null && !request.name().isBlank()) {
            house.setName(request.name());
        }
        if (request.address() != null && !request.address().isBlank()) {
            house.setAddress(request.address());
        }
        return houseRepository.save(house);
    }

    @Transactional
    public Device updateDevice(UUID deviceId, UpdateDeviceRequest request) {
        Device device = deviceRepository.findById(deviceId)
            .orElseThrow(() -> new ResourceNotFoundException("Device not found: " + deviceId));

        if (request.name() != null && !request.name().isBlank()) {
            device.setName(request.name());
        }

        if (request.roomId() != null && !request.roomId().equals(device.getRoom().getId())) {
            Room newRoom = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + request.roomId()));
            device.setRoom(newRoom);
        }

        if (isHeatingRelay(device.getModel()) && device.getSensorExternalId() != null) {
            MonolithSensorUpdateRequest monolithRequest = new MonolithSensorUpdateRequest(
                device.getName(),
                HEATING_TYPE,
                device.getRoom().getName(),
                null,
                UNIT_CELSIUS,
                ""
            );

            try {
                monolithClient.updateSensor(device.getSensorExternalId(), monolithRequest);
            } catch (Exception ex) {
                log.warn("Failed to update sensor {} in monolith: {}", device.getSensorExternalId(), ex.getMessage());
            }
        }

        return deviceRepository.save(device);
    }

}
