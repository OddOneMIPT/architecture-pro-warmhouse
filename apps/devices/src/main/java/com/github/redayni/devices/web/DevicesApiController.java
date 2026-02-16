package com.github.redayni.devices.web;

import com.github.redayni.devices.domain.Device;
import com.github.redayni.devices.domain.House;
import com.github.redayni.devices.service.DeviceCommandService;
import com.github.redayni.devices.service.DeviceQueryService;
import com.github.redayni.devices.web.dto.DeviceResponse;
import com.github.redayni.devices.web.dto.DtoMapper;
import com.github.redayni.devices.web.dto.HouseResponse;
import com.github.redayni.devices.web.dto.InstallModuleRequest;
import com.github.redayni.devices.web.dto.UpdateDeviceRequest;
import com.github.redayni.devices.web.dto.UpdateHouseRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class DevicesApiController {

    private static final Logger log = LoggerFactory.getLogger(DevicesApiController.class);

    private final DeviceCommandService commandService;
    private final DeviceQueryService queryService;

    public DevicesApiController(DeviceCommandService commandService, DeviceQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping("/rooms/{roomId}/install-module")
    @ResponseStatus(HttpStatus.CREATED)
    public List<DeviceResponse> installModule(
        @PathVariable("roomId") UUID roomId,
        @RequestBody @Valid InstallModuleRequest request
    ) {
        List<Device> devices = commandService.installModuleInRoom(roomId, request.moduleId());
        return devices.stream()
            .map(DtoMapper::toDeviceResponse)
            .toList();
    }

    @GetMapping("/houses")
    public ResponseEntity<List<HouseResponse>> getHouses() {
        List<House> houses = queryService.getHouses();
        List<HouseResponse> responses = houses.stream().map(DtoMapper::toHouseResponse).toList();
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/houses/{houseId}")
    public HouseResponse updateHouse(
        @PathVariable("houseId") UUID houseId,
        @RequestBody @Valid UpdateHouseRequest request
    ) {
        House updated = commandService.updateHouse(houseId, request);
        return DtoMapper.toHouseResponse(updated);
    }

    @DeleteMapping("/devices/{deviceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDevice(@PathVariable("deviceId") UUID deviceId) {
        commandService.deleteDevice(deviceId);
    }

    @GetMapping("/houses/{houseId}/devices")
    public ResponseEntity<List<DeviceResponse>> getDevicesByHouse(@PathVariable UUID houseId) {
        List<Device> devices = queryService.getDevicesByHouse(houseId);
        List<DeviceResponse> responses = devices.stream().map(DtoMapper::toDeviceResponse).toList();
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/devices/{deviceId}")
    @ResponseStatus(HttpStatus.OK)
    public DeviceResponse updateDevice(
        @PathVariable UUID deviceId,
        @RequestBody UpdateDeviceRequest request
    ) {
        Device updated = commandService.updateDevice(deviceId, request);
        return DtoMapper.toDeviceResponse(updated);
    }

}
