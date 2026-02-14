package com.example.demo.service;

import com.example.demo.common.exception.DataAlreadyExistsException;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.entity.timeRules.Shift;
import com.example.demo.entity.timeRules.ShiftResponse;
import com.example.demo.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShiftService {
    private final ShiftRepository repository;

    public List<ShiftResponse> getAll() {
        return repository.findAll().stream()
                .map(this::fromTable)
                .collect(Collectors.toList());
    }

    public ShiftResponse getById(Long id) {
        Shift shift = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("الوردية غير موجودة"));
        return fromTable(shift);
    }

    public ShiftResponse save(Shift shift) {
        if (shift.getShiftName() != null && repository.existsByShiftName(shift.getShiftName())) {
            throw new DataAlreadyExistsException("اسم الوردية موجود بالفعل");
        }
        return fromTable(repository.save(shift));
    }

    public ShiftResponse update(Long id, Shift shiftData) {
        Shift existingShift = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("الوردية غير موجودة"));

        if (shiftData.getShiftName() != null && !existingShift.getShiftName().equals(shiftData.getShiftName())
                && repository.existsByShiftName(shiftData.getShiftName())) {
            throw new DataAlreadyExistsException("اسم الوردية موجود بالفعل");
        }

        existingShift.setShiftName(shiftData.getShiftName());
        existingShift.setStartTime(shiftData.getStartTime());
        existingShift.setEndTime(shiftData.getEndTime());
        existingShift.setLateThreshold(shiftData.getLateThreshold());
        existingShift.setEarlyLeaveThreshold(shiftData.getEarlyLeaveThreshold());

        return fromTable(repository.save(existingShift));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("الوردية غير موجودة");
        }
        repository.deleteById(id);
    }

    private ShiftResponse fromTable(Shift item) {
        return new ShiftResponse(
                item.getId(),
                item.getShiftName(),
                item.getStartTime(),
                item.getEndTime(),
                item.getLateThreshold(),
                item.getEarlyLeaveThreshold()
        );
    }
}
