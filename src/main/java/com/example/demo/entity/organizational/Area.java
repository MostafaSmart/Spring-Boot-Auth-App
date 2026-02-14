package com.example.demo.entity.organizational;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    private String areaName;
    private String locationDetails;

    public Area(Long id, String areaName, String locationDetails) {
        this.id = id;
        this.areaName = areaName;
        this.locationDetails = locationDetails;
    }
    @OneToMany(mappedBy = "area")
    private List<Employee> employees;
}

