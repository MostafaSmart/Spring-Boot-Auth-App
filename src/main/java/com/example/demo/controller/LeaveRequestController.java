package com.example.demo.controller;

import com.example.demo.entity.Exceptions.LeaveRequest;
import com.example.demo.entity.Exceptions.LeaveRequestStatus;
import com.example.demo.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {
    private final LeaveRequestService service;

    @GetMapping
    public ResponseEntity<List<LeaveRequest>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveRequest>> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(service.getByEmployee(employeeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequest> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<LeaveRequest> create(@RequestBody LeaveRequest leaveRequest) {
        return ResponseEntity.ok(service.save(leaveRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveRequest> update(@PathVariable Long id, @RequestBody LeaveRequest leaveRequest) {
        return ResponseEntity.ok(service.update(id, leaveRequest));
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<LeaveRequest> updateStatus(@PathVariable Long id, @RequestParam LeaveRequestStatus status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
