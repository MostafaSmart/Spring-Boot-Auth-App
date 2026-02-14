package com.example.demo.entity.organizational;

import lombok.Data;

@Data
public class EmployeeResponse {
    private Long id;


    private String employeeCode;
    private String name;
    private String status;
    private String areaName;
    private String deptName;


    public EmployeeResponse(Long id, String areaName, String status, String name, String employeeCode, String deptName) {
        this.id = id;
        this.areaName = areaName;
        this.status = status;
        this.name = name;
        this.employeeCode = employeeCode;
        this.deptName = deptName;
    }

}
