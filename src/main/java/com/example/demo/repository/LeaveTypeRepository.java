package com.example.demo.repository;

import com.example.demo.entity.Exceptions.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
    boolean existsByTypeName(String typeName);
}
