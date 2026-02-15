package com.example.demo.repository;

import com.example.demo.entity.timeRules.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.organizational.Employee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    boolean existsByEmployeeAndDayOfWeek(Employee employee, DayOfWeek dayOfWeek);

    @Query("SELECT s FROM Schedule s WHERE s.employee.employeeCode = :employeeCode")
    Optional<Schedule> findByEmployeeAndDayOfWeek(@Param("employeeCode") String employeeCode, @Param("dayOfWeek") java.time.DayOfWeek dayOfWeek);
}
