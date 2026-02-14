package com.example.demo.controller;

import com.example.demo.entity.Exceptions.LeaveType;
import com.example.demo.service.LeaveTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leave-types")
@RequiredArgsConstructor
public class LeaveTypeController {
    private final LeaveTypeService service;

    @GetMapping
    public ResponseEntity<List<LeaveType>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveType> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<LeaveType> create(@RequestBody LeaveType leaveType) {
        return ResponseEntity.ok(service.save(leaveType));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveType> update(@PathVariable Long id, @RequestBody LeaveType leaveType) {
        return ResponseEntity.ok(service.update(id, leaveType));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
