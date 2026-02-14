package com.example.demo.repository;

import com.example.demo.entity.timeRules.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.organizational.Employee;
import java.time.DayOfWeek;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    boolean existsByEmployeeAndDayOfWeek(Employee employee, DayOfWeek dayOfWeek);
}
