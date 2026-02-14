package com.example.demo.repository;

import com.example.demo.entity.transactions.AttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long> {

    @Query("SELECT MIN(a.logTime) FROM AttendanceLog a WHERE a.employeeCode = :employeeCode AND a.logTime BETWEEN :start AND :end")
    Optional<LocalDateTime> findFirstLog(@Param("employeeCode") String employeeCode, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT MAX(a.logTime) FROM AttendanceLog a WHERE a.employeeCode = :employeeCode AND a.logTime BETWEEN :start AND :end")
    Optional<LocalDateTime> findLastLog(@Param("employeeCode") String employeeCode, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
