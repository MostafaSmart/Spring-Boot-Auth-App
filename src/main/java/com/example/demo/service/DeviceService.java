package com.example.demo.service;

import com.example.demo.common.exception.DataAlreadyExistsException;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.entity.Device.Device;
import com.example.demo.entity.organizational.Area;
import com.example.demo.repository.AreaRepository;
import com.example.demo.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository repository;
    private final AreaRepository areaRepository;

    public List<Device> getAll() {
        return repository.findAll();
    }

    public Device getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("الجهاز غير موجود"));
    }

    public Device save(Device device) {
        if (repository.existsBySerialNumber(device.getSerialNumber())) {
            throw new DataAlreadyExistsException("الرقم التسلسلي للجهاز موجود بالفعل");
        }
        if (device.getIpAddress() != null && repository.existsByIpAddress(device.getIpAddress())) {
            throw new DataAlreadyExistsException("عنوان IP موجود بالفعل");
        }

        validateArea(device);

        return repository.save(device);
    }

    public Device update(Long id, Device deviceData) {
        Device existingDevice = getById(id);

        if (!existingDevice.getSerialNumber().equals(deviceData.getSerialNumber())
                && repository.existsBySerialNumber(deviceData.getSerialNumber())) {
            throw new DataAlreadyExistsException("الرقم التسلسلي للجهاز موجود بالفعل");
        }
        if (deviceData.getIpAddress() != null && !deviceData.getIpAddress().equals(existingDevice.getIpAddress())
                && repository.existsByIpAddress(deviceData.getIpAddress())) {
            throw new DataAlreadyExistsException("عنوان IP موجود بالفعل");
        }

        validateArea(deviceData);

        existingDevice.setSerialNumber(deviceData.getSerialNumber());
        existingDevice.setDeviceName(deviceData.getDeviceName());
        existingDevice.setIpAddress(deviceData.getIpAddress());
        existingDevice.setPort(deviceData.getPort());
        existingDevice.setStatus(deviceData.getStatus());
        existingDevice.setArea(deviceData.getArea()); // managed in validateArea or just set if valid

        return repository.save(existingDevice);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("الجهاز غير موجود");
        }
        repository.deleteById(id);
    }

    private void validateArea(Device device) {
        if (device.getArea() != null && device.getArea().getId() != null) {
            Area area = areaRepository.findById(device.getArea().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("الفرع غير موجود"));
            device.setArea(area);
        }
    }
}
