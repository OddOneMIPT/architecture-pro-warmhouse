package com.github.redayni.devices.repository;

import com.github.redayni.devices.domain.ModuleDeviceModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ModuleDeviceModelRepository extends JpaRepository<ModuleDeviceModel, UUID> {

    List<ModuleDeviceModel> findByModuleId(UUID moduleId);
}
