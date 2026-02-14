package com.example.demo.entity.Device;

import com.example.demo.entity.organizational.Area;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String serialNumber; // الرقم التسلسلي للجهاز (مهم جداً للربط)

    private String deviceName; // اسم وصفي (مثلاً: جهاز بوابة الاستقبال)

    private String ipAddress;  // عنوان الـ IP الخاص بالجهاز

    private Integer port;      // المنفذ (غالباً 4370)

    private LocalDateTime lastActivity; // آخر وقت تواصل فيه الجهاز مع السيرفر

    @Enumerated(EnumType.STRING)
    private DeviceStatus status; // (ONLINE, OFFLINE)

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id")
    private Area area; // الجهاز يتبع فرعاً معيناً
}

