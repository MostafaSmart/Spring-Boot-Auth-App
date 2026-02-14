package com.example.demo.controller;

import com.example.demo.entity.Device.Device;
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
    public ResponseEntity<List<Device>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Device> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Device> create(@RequestBody Device device) {
        return ResponseEntity.ok(service.save(device));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Device> update(@PathVariable Long id, @RequestBody Device device) {
        return ResponseEntity.ok(service.update(id, device));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/push-logs")
    public ResponseEntity<String> pushLogs(@RequestBody com.example.demo.common.dto.DeviceLogDto logDto) {
        // Validate Device
        if (!service.validateDevice(logDto.getSerialNumber())) {
            return ResponseEntity.badRequest().body("Invalid Device Serial Number");
        }

        // Process Log
        attendanceService.processRawLog(logDto.getEmployeeCode(), logDto.getLogTime(), logDto.getSerialNumber());
        return ResponseEntity.ok("Log Received");
    }
}
