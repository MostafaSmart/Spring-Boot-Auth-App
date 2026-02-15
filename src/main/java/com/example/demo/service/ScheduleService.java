package com.example.demo.service;

import com.example.demo.common.exception.DataAlreadyExistsException;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.entity.organizational.Employee;
import com.example.demo.entity.timeRules.Schedule;
import com.example.demo.entity.timeRules.ScheduleResponse;
import com.example.demo.entity.timeRules.Shift;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.ScheduleRepository;
import com.example.demo.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository repository;
    private final EmployeeRepository employeeRepository;
    private final ShiftRepository shiftRepository;

    public List<ScheduleResponse> getAll() {
        return repository.findAll().stream()
                .map(this::fromTable)
                .collect(Collectors.toList());
    }

    public ScheduleResponse getById(Long id) {
        Schedule schedule = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("الجدول غير موجود"));
        return fromTable(schedule);
    }

    public ScheduleResponse save(Schedule schedule) {
        validateRelations(schedule);

        // Check for duplicate schedule
        if (repository.existsByEmployeeAndDayOfWeek(schedule.getEmployee(), schedule.getDayOfWeek())) {
            throw new DataAlreadyExistsException("يوجد بالفعل جدول لهذا الموظف في هذا اليوم");
        }

        return fromTable(repository.save(schedule));
    }

    public ScheduleResponse update(Long id, Schedule scheduleData) {
        Schedule existingSchedule = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("الجدول غير موجود"));

        // Validate relations first (Employee and Shift existence)
        Employee employee = null;
        if (scheduleData.getEmployee() != null && scheduleData.getEmployee().getId() != null) {
             employee = employeeRepository.findById(scheduleData.getEmployee().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("الموظف غير موجود"));
             existingSchedule.setEmployee(employee);
        }

        if (scheduleData.getShift() != null && scheduleData.getShift().getId() != null) {
            Shift shift = shiftRepository.findById(scheduleData.getShift().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("الوردية غير موجودة"));
            existingSchedule.setShift(shift);
        }

        if (scheduleData.getDayOfWeek() != null) {
            // Check if changing day or employee causes a conflict
            // Note: If we just updated existingSchedule.setEmployee above, we should use that.
            // If employee didn't change, use existingSchedule.getEmployee().
            // Same for DayOfWeek.
            
            Employee checkEmployee = (employee != null) ? employee : existingSchedule.getEmployee();
            
            // Only check if day is changing OR employee is changing
            boolean isDayChanged = !scheduleData.getDayOfWeek().equals(existingSchedule.getDayOfWeek());
            boolean isEmployeeChanged = (employee != null) && !employee.getId().equals(existingSchedule.getEmployee().getId());

            if ((isDayChanged || isEmployeeChanged) &&
                    repository.existsByEmployeeAndDayOfWeek(checkEmployee, scheduleData.getDayOfWeek())) {
                 throw new DataAlreadyExistsException("يوجد بالفعل جدول لهذا الموظف في هذا اليوم");
            }
            existingSchedule.setDayOfWeek(scheduleData.getDayOfWeek());
        }

        return fromTable(repository.save(existingSchedule));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("الجدول غير موجود");
        }
        repository.deleteById(id);
    }

    private void validateRelations(Schedule schedule) {
        if (schedule.getEmployee() != null && schedule.getEmployee().getId() != null) {
            Employee emp = employeeRepository.findById(schedule.getEmployee().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("الموظف غير موجود"));
            schedule.setEmployee(emp); // Set the managed entity
        } else {
             throw new ResourceNotFoundException("يجب اختيار الموظف");
        }

        if (schedule.getShift() != null && schedule.getShift().getId() != null) {
            Shift shift = shiftRepository.findById(schedule.getShift().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("الوردية غير موجودة"));
            schedule.setShift(shift); // Set the managed entity
        } else {
             throw new ResourceNotFoundException("يجب اختيار الوردية");
        }
    }

    public List<ScheduleResponse> getAllGroupedByEmployee() {
        List<Schedule> allSchedules = repository.findAll();

        // 1. التجميع بناءً على مفتاح فريد (الموظف + الوردية)
        Map<String, List<Schedule>> grouped = allSchedules.stream()
                .collect(Collectors.groupingBy(s ->
                        s.getEmployee().getId() + "-" + s.getShift().getId()
                ));

        // 2. تحويل الخريطة (Map) إلى قائمة الرد المطلوبة (List<ScheduleResponse>)
        return grouped.values().stream().map(schedules -> {
            // نأخذ أول سجل لاستخلاص بيانات الموظف والوردية (كلهم متشابهون في نفس المجموعة)
            Schedule first = schedules.get(0);

            return ScheduleResponse.builder()
                    .employeeId(first.getEmployee().getId())
                    .employeeName(first.getEmployee().getName())
                    .employeeCode(first.getEmployee().getEmployeeCode())
                    .shiftId(first.getShift().getId())
                    .shiftName(first.getShift().getShiftName())
                    .shiftStart(first.getShift().getStartTime().toString())
                    .shiftEnd(first.getShift().getEndTime().toString())
                    // تجميع الأيام من كافة سجلات هذه المجموعة
                    .daysOfWeek(schedules.stream()
                            .map(Schedule::getDayOfWeek)
                            .sorted() // اختياري: لترتيب الأيام (الأحد، الاثنين...)
                            .collect(Collectors.toList()))
                    .build();
        }).collect(Collectors.toList());
    }

    private ScheduleResponse fromTable(Schedule item) {
        String employeeName = (item.getEmployee() != null) ? item.getEmployee().getName() : "Unknown";
        String shiftName = (item.getShift() != null) ? item.getShift().getShiftName() : "Unknown";
        Long employeeId = (item.getEmployee() != null) ? item.getEmployee().getId() : null;
        String employeeCode = (item.getEmployee() != null) ? item.getEmployee().getEmployeeCode() : null;

        Long shiftId = (item.getShift() != null) ? item.getShift().getId() : null;
        LocalTime start = (item.getShift() != null) ? item.getShift().getStartTime() : null;
        LocalTime end = (item.getShift() != null) ? item.getShift().getEndTime() : null;

        assert end != null;
        return new ScheduleResponse(
//                item.getId(),
//                employeeId,
//                employeeName,
//                employeeCode,
//                shiftId,
//                shiftName,
//                start.toString(),
//                end.toString(),
//                item.getDayOfWeek()
        );
    }

}
