package com.example.demo.controller;

import com.example.demo.entity.timeRules.Schedule;
import com.example.demo.entity.timeRules.ScheduleResponse;
import com.example.demo.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService service;

    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> getAll() {
        return ResponseEntity.ok(service.getAllGroupedByEmployee());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<ScheduleResponse> create(@RequestBody Schedule schedule) {
        return ResponseEntity.ok(service.save(schedule));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponse> update(@PathVariable Long id, @RequestBody Schedule schedule) {
        return ResponseEntity.ok(service.update(id, schedule));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
