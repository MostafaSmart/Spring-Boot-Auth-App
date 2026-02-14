package com.example.demo.service;

import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.entity.Exceptions.LeaveRequest;
import com.example.demo.entity.Exceptions.LeaveRequestStatus;
import com.example.demo.entity.Exceptions.LeaveType;
import com.example.demo.entity.organizational.Employee;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.LeaveRequestRepository;
import com.example.demo.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {
    private final LeaveRequestRepository repository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    public List<LeaveRequest> getAll() {
        return repository.findAll();
    }

    public LeaveRequest getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("طلب الإجازة غير موجود"));
    }

    public List<LeaveRequest> getByEmployee(Long employeeId) {
        return repository.findByEmployeeId(employeeId);
    }

    public LeaveRequest save(LeaveRequest leaveRequest) {
        validateRelations(leaveRequest);
        validateDates(leaveRequest);

        if (leaveRequest.getStatus() == null) {
            leaveRequest.setStatus(LeaveRequestStatus.PENDING);
        }

        return repository.save(leaveRequest);
    }

    public LeaveRequest update(Long id, LeaveRequest leaveRequestData) {
        LeaveRequest existingRequest = getById(id);

        if (leaveRequestData.getStartDate() != null) existingRequest.setStartDate(leaveRequestData.getStartDate());
        if (leaveRequestData.getEndDate() != null) existingRequest.setEndDate(leaveRequestData.getEndDate());
        if (leaveRequestData.getStatus() != null) existingRequest.setStatus(leaveRequestData.getStatus());
        
        // If employee or leave type changes, validate them
        if (leaveRequestData.getEmployee() != null && leaveRequestData.getEmployee().getId() != null) {
             Employee emp = employeeRepository.findById(leaveRequestData.getEmployee().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("الموظف غير موجود"));
             existingRequest.setEmployee(emp);
        }
        if (leaveRequestData.getLeaveType() != null && leaveRequestData.getLeaveType().getId() != null) {
             LeaveType type = leaveTypeRepository.findById(leaveRequestData.getLeaveType().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("نوع الإجازة غير موجود"));
             existingRequest.setLeaveType(type);
        }
        
        validateDates(existingRequest);

        return repository.save(existingRequest);
    }
    
    public LeaveRequest updateStatus(Long id, LeaveRequestStatus status) {
        LeaveRequest existingRequest = getById(id);
        existingRequest.setStatus(status);
        return repository.save(existingRequest);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("طلب الإجازة غير موجود");
        }
        repository.deleteById(id);
    }

    private void validateRelations(LeaveRequest request) {
        if (request.getEmployee() != null && request.getEmployee().getId() != null) {
            Employee emp = employeeRepository.findById(request.getEmployee().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("الموظف غير موجود"));
            request.setEmployee(emp);
        } else {
            throw new ResourceNotFoundException("يجب تحديد الموظف");
        }

        if (request.getLeaveType() != null && request.getLeaveType().getId() != null) {
            LeaveType type = leaveTypeRepository.findById(request.getLeaveType().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("نوع الإجازة غير موجود"));
            request.setLeaveType(type);
        } else {
            throw new ResourceNotFoundException("يجب تحديد نوع الإجازة");
        }
    }

    private void validateDates(LeaveRequest request) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new IllegalArgumentException("يجب تحديد تاريخ البداية والنهاية");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("تاريخ النهاية يجب أن يكون بعد تاريخ البداية");
        }
    }
}
