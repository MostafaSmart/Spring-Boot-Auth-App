package com.example.demo.controller;

import com.example.demo.common.dto.DeviceLogDto;
import com.example.demo.entity.Device.Device;
import com.example.demo.entity.Device.DeviceResponse;
import com.example.demo.entity.other.StringResponse;
import com.example.demo.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService service;
    private final com.example.demo.service.AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<List<DeviceResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<DeviceResponse> create(@RequestBody Device device) {
        return ResponseEntity.ok(service.save(device));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponse> update(@PathVariable Long id, @RequestBody Device device) {
        return ResponseEntity.ok(service.update(id, device));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/push-logs")
    public ResponseEntity<StringResponse> pushLogs(@RequestBody DeviceLogDto logDto) {
        // Validate Device
        if (!service.validateDevice(logDto.getSerialNumber())) {
            return ResponseEntity.badRequest().body(new StringResponse("Invalid Device Serial Number"));
        }

        // Process Log
        attendanceService.processRawLog(logDto);
        return ResponseEntity.ok(new StringResponse("OK"));
    }
}
