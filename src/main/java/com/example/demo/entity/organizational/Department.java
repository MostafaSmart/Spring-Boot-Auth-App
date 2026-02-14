package com.example.demo.entity.organizational;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @NoArgsConstructor
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String deptName;

    @ManyToOne
    @JoinColumn(name = "parent_dept_id")
    private Department parentDepartment; // للهيكلية الشجرية
}
