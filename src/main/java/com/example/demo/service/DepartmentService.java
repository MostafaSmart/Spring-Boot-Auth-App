package com.example.demo.service;

import com.example.demo.common.exception.DataAlreadyExistsException;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.entity.organizational.Department;
import com.example.demo.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository repository;

    public List<Department> getAll() {
        return repository.findAll();
    }

    public Department getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("القسم غير موجود"));
    }

    public Department save(Department department) {
        if (repository.existsByDeptName(department.getDeptName())) {
            throw new DataAlreadyExistsException("اسم القسم موجود بالفعل");
        }
        // Handle parent department logic if necessary, e.g. verify parent exists
        if (department.getParentDepartment() != null && department.getParentDepartment().getId() != null) {
            repository.findById(department.getParentDepartment().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("القسم الرئيسي غير موجود"));
        }

        return repository.save(department);
    }

    public Department update(Long id, Department department) {
        Department existingDept = getById(id);

        if (!existingDept.getDeptName().equals(department.getDeptName())
                && repository.existsByDeptName(department.getDeptName())) {
            throw new DataAlreadyExistsException("اسم القسم موجود بالفعل");
        }

        existingDept.setDeptName(department.getDeptName());

        if (department.getParentDepartment() != null) {
            Department parent = repository.findById(department.getParentDepartment().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("القسم الرئيسي غير موجود"));
            existingDept.setParentDepartment(parent);
        } else {
            existingDept.setParentDepartment(null);
        }

        return repository.save(existingDept);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("القسم غير موجود");
        }
        repository.deleteById(id);
    }
}
