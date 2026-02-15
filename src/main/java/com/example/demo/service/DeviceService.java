package com.example.demo.service;

import com.example.demo.common.exception.DataAlreadyExistsException;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.entity.Device.Device;
import com.example.demo.entity.Device.DeviceResponse;
import com.example.demo.entity.organizational.Area;
import com.example.demo.repository.AreaRepository;
import com.example.demo.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository repository;
    private final AreaRepository areaRepository;

    public List<DeviceResponse> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public DeviceResponse getById(Long id) {
        Device device = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("الجهاز غير موجود"));
        return toResponse(device);
    }

    public DeviceResponse save(Device device) {
        if (repository.existsBySerialNumber(device.getSerialNumber())) {
            throw new DataAlreadyExistsException("الرقم التسلسلي للجهاز موجود بالفعل");
        }
        if (device.getIpAddress() != null && repository.existsByIpAddress(device.getIpAddress())) {
            throw new DataAlreadyExistsException("عنوان IP موجود بالفعل");
        }

        validateArea(device);
        Device savedDevice = repository.save(device);
        return toResponse(savedDevice);
    }

    public DeviceResponse update(Long id, Device deviceData) {
        Device existingDevice = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("الجهاز غير موجود"));

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
        existingDevice.setArea(deviceData.getArea());

        if(deviceData.getLastActivity() != null) {
            existingDevice.setLastActivity(deviceData.getLastActivity());
        }

        Device updatedDevice = repository.save(existingDevice);
        return toResponse(updatedDevice);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("الجهاز غير موجود");
        }
        repository.deleteById(id);
    }

    private DeviceResponse toResponse(Device device) {
        String areaName = (device.getArea() != null) ? device.getArea().getAreaName() : "غير محدد";

        return new DeviceResponse(
                device.getId(),
                device.getSerialNumber(),
                device.getIpAddress(),
                device.getDeviceName(),
                device.getPort(),
                device.getLastActivity(),
                device.getStatus(),
                areaName
        );
    }

    private void validateArea(Device device) {
        if (device.getArea() != null && device.getArea().getId() != null) {
            Area area = areaRepository.findById(device.getArea().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("الفرع غير موجود"));
            device.setArea(area);
        }
    }

    public boolean validateDevice(String serialNumber) {
        return repository.existsBySerialNumber(serialNumber);
    }
}
