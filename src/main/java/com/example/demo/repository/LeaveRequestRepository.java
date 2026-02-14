package com.example.demo.repository;

import com.example.demo.entity.Exceptions.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployeeId(Long employeeId);

    @Query("SELECT l FROM LeaveRequest l WHERE l.employee.employeeCode = :employeeCode " +
           "AND :date BETWEEN l.startDate AND l.endDate " +
           "AND l.status = com.example.demo.entity.Exceptions.LeaveRequestStatus.APPROVED")
    Optional<LeaveRequest> findApprovedLeave(@Param("employeeCode") String employeeCode, @Param("date") java.time.LocalDate date);
}
