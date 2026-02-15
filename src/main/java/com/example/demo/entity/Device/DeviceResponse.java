package com.example.demo.entity.Device;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class DeviceResponse {
    private Long id;
    private String serialNumber; // الرقم التسلسلي للجهاز (مهم جداً للربط)

    private String deviceName; // اسم وصفي (مثلاً: جهاز بوابة الاستقبال)

    private String ipAddress;  // عنوان الـ IP الخاص بالجهاز


    private Integer port;      // المنفذ (غالباً 4370)

    private LocalDateTime lastActivity; // آخر وقت تواصل فيه الجهاز مع السيرفر
    private DeviceStatus status; // (ONLINE, OFFLINE)

    private String areaName;



    public DeviceResponse(Long id, String serialNumber, String ipAddress, String deviceName, Integer port, LocalDateTime lastActivity, DeviceStatus status, String areaName) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.ipAddress = ipAddress;
        this.deviceName = deviceName;
        this.port = port;
        this.lastActivity = lastActivity;
        this.status = status;
        this.areaName = areaName;
    }

}

