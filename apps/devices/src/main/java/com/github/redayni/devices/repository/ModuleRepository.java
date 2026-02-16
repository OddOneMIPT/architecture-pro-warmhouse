package com.github.redayni.devices.repository;

import com.github.redayni.devices.domain.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ModuleRepository extends JpaRepository<Module, UUID> {
}
