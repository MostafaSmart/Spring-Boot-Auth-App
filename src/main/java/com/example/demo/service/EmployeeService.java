package com.example.demo.service;

import com.example.demo.common.exception.DataAlreadyExistsException;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.entity.organizational.*;
import com.example.demo.repository.AreaRepository;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository repository;
    private final AreaRepository areaRepository;
    private final DepartmentRepository departmentRepository;


    public List<EmployeeResponse> getAll() {
        return repository.findAll().stream()
                .map(this::fromTable)
                .collect(Collectors.toList());

    }

    public EmployeeResponse getById(Long id) {
        Employee employee = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("الموظف غير موجود"));
        return fromTable(employee);
    }


    public EmployeeResponse save(Employee employee) {
        if (repository.existsByEmployeeCode(employee.getEmployeeCode())) {
            throw new DataAlreadyExistsException("كود الموظف موجود بالفعل");
        }

        validateRelations(employee);

        return fromTable(repository.save(employee));
    }


    public EmployeeResponse update(Long id, Employee employeeData) {
        // 1. جلب الموظف الحقيقي من قاعدة البيانات (Entity وليس DTO)
        Employee existingEmployee = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("الموظف غير موجود"));

        // 2. التحقق من كود الموظف إذا تم تغييره
        if (!existingEmployee.getEmployeeCode().equals(employeeData.getEmployeeCode())
                && repository.existsByEmployeeCode(employeeData.getEmployeeCode())) {
            throw new DataAlreadyExistsException("كود الموظف موجود بالفعل");
        }

        // 3. تحديث البيانات الأساسية
        existingEmployee.setName(employeeData.getName());
        existingEmployee.setEmployeeCode(employeeData.getEmployeeCode());
        existingEmployee.setStatus(employeeData.getStatus());

        // 4. تحديث علاقة المنطقة (Area)
        if (employeeData.getArea() != null && employeeData.getArea().getId() != null) {
            Area area = areaRepository.findById(employeeData.getArea().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("المنطقة المختارة غير موجودة"));
            existingEmployee.setArea(area);
        } else {
            existingEmployee.setArea(null);
        }

        // 5. تحديث علاقة القسم (Department)
        if (employeeData.getDepartment() != null && employeeData.getDepartment().getId() != null) {
            Department dept = departmentRepository.findById(employeeData.getDepartment().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("القسم المختار غير موجود"));
            existingEmployee.setDepartment(dept);
        } else {
            existingEmployee.setDepartment(null);
        }

        // 6. الحفظ وتحويل النتيجة إلى DTO
        Employee updatedEmployee = repository.save(existingEmployee);
        return fromTable(updatedEmployee);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("الموظف غير موجود");
        }
        repository.deleteById(id);
    }

    private void validateRelations(Employee employee) {
        if (employee.getArea() != null && employee.getArea().getId() != null) {
            areaRepository.findById(employee.getArea().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("المنطقة غير موجودة"));
        }
        if (employee.getDepartment() != null && employee.getDepartment().getId() != null) {
            departmentRepository.findById(employee.getDepartment().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("القسم غير موجود"));
        }
    }


    private EmployeeResponse fromTable(Employee item) {
        String areaName = (item.getArea() != null) ? item.getArea().getAreaName() : "غير محدد";
        String deptName = (item.getDepartment() != null) ? item.getDepartment().getDeptName() : "غير محدد";

        return new EmployeeResponse(
                item.getId(),
                areaName,
                item.getStatus(),
                item.getName(),
                item.getEmployeeCode(),
                deptName
        );
    }


}
