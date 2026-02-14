package com.example.demo.repository;

import com.example.demo.entity.organizational.Area;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AreaRepository extends JpaRepository<Area, Long> {
    boolean existsByAreaName(String areaName);
}
