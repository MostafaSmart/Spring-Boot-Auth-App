package com.example.demo.repository;

import com.example.demo.entity.transactions.DailyAttendanceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface DailyAttendanceReportRepository extends JpaRepository<DailyAttendanceReport, Long>, JpaSpecificationExecutor<DailyAttendanceReport> {
    Optional<DailyAttendanceReport> findByEmployee_EmployeeCodeAndDate(String employeeCode, LocalDate date);
}
