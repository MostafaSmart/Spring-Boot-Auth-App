package com.example.demo.service;

import com.example.demo.common.dto.DailyDetailDto;
import com.example.demo.common.dto.ExceptionReportDto;
import com.example.demo.common.dto.MonthlySummaryDto;
import com.example.demo.entity.transactions.DailyAttendanceReport;
import com.example.demo.repository.DailyAttendanceReportRepository;
import com.example.demo.repository.specification.AttendanceSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final DailyAttendanceReportRepository repository;

    public Page<DailyDetailDto> getDailyDetailReport(
            LocalDate startDate, LocalDate endDate, Long employeeId, Long areaId, Long departmentId, String status, Pageable pageable) {

        Specification<DailyAttendanceReport> spec = AttendanceSpecification.getReport(startDate, endDate, employeeId, areaId, departmentId, status);
        Page<DailyAttendanceReport> page = repository.findAll(spec, pageable);

        return page.map(this::mapToDailyDetailDto);
    }

    public List<MonthlySummaryDto> getMonthlySummaryReport(
            LocalDate startDate, LocalDate endDate, Long employeeId, Long areaId, Long departmentId) {

        Specification<DailyAttendanceReport> spec = AttendanceSpecification.getReport(startDate, endDate, employeeId, areaId, departmentId, null);
        List<DailyAttendanceReport> reports = repository.findAll(spec);

        Map<String, List<DailyAttendanceReport>> groupedByEmployee = reports.stream()
                .collect(Collectors.groupingBy(r -> r.getEmployee().getEmployeeCode()));

        return groupedByEmployee.entrySet().stream().map(entry -> {
            List<DailyAttendanceReport> empReports = entry.getValue();
            return mapToMonthlySummaryDto(empReports);
        }).collect(Collectors.toList());
    }

    public Page<ExceptionReportDto> getExceptionReport(
            LocalDate startDate, LocalDate endDate, Long employeeId, Long areaId, Long departmentId, Pageable pageable) {
        
        // Filter for exceptions only
        Specification<DailyAttendanceReport> spec = AttendanceSpecification.getReport(startDate, endDate, employeeId, areaId, departmentId, null);
        // Add exception status filter
        spec = spec.and((root, query, cb) -> cb.notEqual(root.get("status"), "PRESENT"));

        Page<DailyAttendanceReport> page = repository.findAll(spec, pageable);
        return page.map(this::mapToExceptionReportDto);
    }

    // Excel Export Logic
    public ByteArrayInputStream exportDailyReportToExcel(List<DailyDetailDto> data) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Daily Report");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"Employee Code", "Name", "Date", "Check-In", "Check-Out", "Status", "Late (min)", "Early (min)", "Overtime (min)"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (DailyDetailDto dto : data) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(dto.getEmployeeCode());
                row.createCell(1).setCellValue(dto.getEmployeeName());
                row.createCell(2).setCellValue(dto.getDate().toString());
                row.createCell(3).setCellValue(dto.getCheckIn() != null ? dto.getCheckIn().toLocalTime().toString() : "-");
                row.createCell(4).setCellValue(dto.getCheckOut() != null ? dto.getCheckOut().toLocalTime().toString() : "-");
                row.createCell(5).setCellValue(dto.getStatus());
                row.createCell(6).setCellValue(dto.getLateMinutes());
                row.createCell(7).setCellValue(dto.getEarlyOutMinutes());
                row.createCell(8).setCellValue(dto.getOvertimeMinutes());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Fail to import data to Excel file: " + e.getMessage());
        }
    }

    // Mappers
    private DailyDetailDto mapToDailyDetailDto(DailyAttendanceReport report) {
        return DailyDetailDto.builder()
                .employeeName(report.getEmployee().getName())
                .employeeCode(report.getEmployee().getEmployeeCode())
                .date(report.getDate())
                .checkIn(report.getCheckIn())
                .checkOut(report.getCheckOut())
                .status(report.getStatus())
                .lateMinutes(report.getLateMinutes() != null ? report.getLateMinutes() : 0)
                .earlyOutMinutes(report.getEarlyOutMinutes() != null ? report.getEarlyOutMinutes() : 0)
                .overtimeMinutes(report.getOvertimeMinutes() != null ? report.getOvertimeMinutes() : 0)
                .build();
    }

    private MonthlySummaryDto mapToMonthlySummaryDto(List<DailyAttendanceReport> reports) {
        if (reports.isEmpty()) return null;
        DailyAttendanceReport first = reports.get(0);
        
        int presentDays = 0;
        int absentDays = 0;
        long totalLate = 0;
        long totalOvertime = 0;
        long totalWork = 0;

        for (DailyAttendanceReport r : reports) {
            if ("PRESENT".equalsIgnoreCase(r.getStatus())) presentDays++;
            if ("ABSENT".equalsIgnoreCase(r.getStatus())) absentDays++;
            totalLate += r.getLateMinutes() != null ? r.getLateMinutes() : 0;
            totalOvertime += r.getOvertimeMinutes() != null ? r.getOvertimeMinutes() : 0;
            totalWork += r.getWorkDurationMinutes() != null ? r.getWorkDurationMinutes() : 0;
        }

        return MonthlySummaryDto.builder()
                .employeeName(first.getEmployee().getName())
                .employeeCode(first.getEmployee().getEmployeeCode())
                .presentDays(presentDays)
                .absentDays(absentDays)
                .totalLateMinutes(totalLate)
                .totalOvertimeMinutes(totalOvertime)
                .totalWorkHours(totalWork / 60)
                .build();
    }

    private ExceptionReportDto mapToExceptionReportDto(DailyAttendanceReport report) {
        String details = "";
        if (report.getLateMinutes() != null && report.getLateMinutes() > 0) details += "Late: " + report.getLateMinutes() + "m ";
        if (report.getEarlyOutMinutes() != null && report.getEarlyOutMinutes() > 0) details += "Early: " + report.getEarlyOutMinutes() + "m ";
        
        return ExceptionReportDto.builder()
                .employeeName(report.getEmployee().getName())
                .employeeCode(report.getEmployee().getEmployeeCode())
                .date(report.getDate())
                .exceptionType(report.getStatus())
                .details(details)
                .build();
    }
}
