package com.github.redayni.devices.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "module_device_models")
public class ModuleDeviceModel {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_model_id", nullable = false)
    private DeviceModel deviceModel;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    protected ModuleDeviceModel() {
    }

    public ModuleDeviceModel(Module module, DeviceModel deviceModel, int quantity) {
        this.id = UUID.randomUUID();
        this.module = module;
        this.deviceModel = deviceModel;
        this.quantity = quantity;
    }

    public UUID getId() {
        return id;
    }

    public Module getModule() {
        return module;
    }

    public DeviceModel getDeviceModel() {
        return deviceModel;
    }

    public int getQuantity() {
        return quantity;
    }
}
