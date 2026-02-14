package com.example.demo.service;

import com.example.demo.common.exception.DataAlreadyExistsException;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.entity.Exceptions.LeaveType;
import com.example.demo.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveTypeService {
    private final LeaveTypeRepository repository;

    public List<LeaveType> getAll() {
        return repository.findAll();
    }

    public LeaveType getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("نوع الإجازة غير موجود"));
    }

    public LeaveType save(LeaveType leaveType) {
        if (repository.existsByTypeName(leaveType.getTypeName())) {
            throw new DataAlreadyExistsException("اسم نوع الإجازة موجود بالفعل");
        }
        return repository.save(leaveType);
    }

    public LeaveType update(Long id, LeaveType leaveTypeData) {
        LeaveType existingType = getById(id);

        if (!existingType.getTypeName().equals(leaveTypeData.getTypeName())
                && repository.existsByTypeName(leaveTypeData.getTypeName())) {
            throw new DataAlreadyExistsException("اسم نوع الإجازة موجود بالفعل");
        }

        existingType.setTypeName(leaveTypeData.getTypeName());
        return repository.save(existingType);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("نوع الإجازة غير موجود");
        }
        repository.deleteById(id);
    }
}
