package com.example.demo.repository;

import com.example.demo.entity.timeRules.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    boolean existsByShiftName(String shiftName);
}
