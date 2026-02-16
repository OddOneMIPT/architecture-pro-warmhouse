package com.github.redayni.devices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.service.registry.ImportHttpServices;

@SpringBootApplication
@ImportHttpServices(basePackages = "com.github.redayni.devices.client")
public class DevicesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevicesServiceApplication.class, args);
    }
}
