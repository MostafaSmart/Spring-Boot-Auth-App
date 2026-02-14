package com.example.demo.controller;

import com.example.demo.entity.organizational.Area;
import com.example.demo.entity.organizational.AreaResponse;
import com.example.demo.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/areas")
@RequiredArgsConstructor
public class AreaController {
    private final AreaService service;

    @GetMapping
    public ResponseEntity<List<AreaResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AreaResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<AreaResponse> create(@RequestBody Area area) {
        return ResponseEntity.ok(service.save(area));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AreaResponse> update(@PathVariable Long id, @RequestBody Area area) {
        return ResponseEntity.ok(service.update(id, area));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
