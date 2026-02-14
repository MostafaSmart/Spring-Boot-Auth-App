package com.example.demo.repository;

import com.example.demo.entity.Device.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    boolean existsBySerialNumber(String serialNumber);
    boolean existsByIpAddress(String ipAddress);
}
