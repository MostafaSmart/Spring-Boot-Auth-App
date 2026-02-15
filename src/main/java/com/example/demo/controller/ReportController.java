package com.example.demo.controller;

import com.example.demo.common.dto.DailyDetailDto;
import com.example.demo.common.dto.ExceptionReportDto;
import com.example.demo.common.dto.MonthlySummaryDto;
import com.example.demo.entity.other.StringResponse;
import com.example.demo.service.AttendanceService;
import com.example.demo.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {
    private final AttendanceService attendanceService;
    private final ReportService reportService;

    @GetMapping("/daily-details")
    public ResponseEntity<Page<DailyDetailDto>> getDailyDetailReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String status,
            Pageable pageable
    ) {
        return ResponseEntity.ok(reportService.getDailyDetailReport(startDate, endDate, employeeId, areaId, departmentId, status, pageable));
    }

    @GetMapping("/monthly-summary")
    public ResponseEntity<List<MonthlySummaryDto>> getMonthlySummaryReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) Long departmentId
    ) {
        return ResponseEntity.ok(reportService.getMonthlySummaryReport(startDate, endDate, employeeId, areaId, departmentId));
    }


    @GetMapping("/exceptions")
    public ResponseEntity<Page<ExceptionReportDto>> getExceptionReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) Long departmentId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(reportService.getExceptionReport(startDate, endDate, employeeId, areaId, departmentId, pageable));
    }
    @IgnoreApiResponse // أضف هذا السطر هنا
    @GetMapping("/download/daily-details")
    public ResponseEntity<InputStreamResource> downloadDailyReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String status
    ) {
        Page<DailyDetailDto> page = reportService.getDailyDetailReport(startDate, endDate, employeeId, areaId, departmentId, status, Pageable.unpaged());
        ByteArrayInputStream in = reportService.exportDailyReportToExcel(page.toList());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=daily_report.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }


    @PostMapping("/calculate")
    public ResponseEntity<StringResponse> triggerCalculation(@RequestParam("date") String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        attendanceService.calculateDailyAttendance(date);
        return ResponseEntity.ok(new StringResponse("تم بدء عملية الاحتساب للتاريخ: " + dateStr));
    }
}


