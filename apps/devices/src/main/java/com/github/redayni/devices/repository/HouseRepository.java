package com.github.redayni.devices.repository;

import com.github.redayni.devices.domain.House;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HouseRepository extends JpaRepository<House, UUID> {
}
