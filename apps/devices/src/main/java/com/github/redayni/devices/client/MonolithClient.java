package com.github.redayni.devices.client;

import com.github.redayni.devices.client.dto.MonolithSensorRequest;
import com.github.redayni.devices.client.dto.MonolithSensorResponse;
import com.github.redayni.devices.client.dto.MonolithSensorUpdateRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

@HttpExchange(url = "http://smarthome-app:8080/api/v1")
public interface MonolithClient {

    @PostExchange("/sensors")
    MonolithSensorResponse createSensor(@RequestBody MonolithSensorRequest request);

    @PutExchange("/sensors/{id}")
    MonolithSensorResponse updateSensor(
        @PathVariable("id") int id,
        @RequestBody MonolithSensorUpdateRequest request
    );

    @DeleteExchange("/sensors/{id}")
    void deleteSensor(@PathVariable("id") int id);
}
