package com.example.demo.common.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DeviceLogDto {
    private String serialNumber;
    private String employeeCode;
    private LocalDateTime logTime;
    private String punchType;

}
