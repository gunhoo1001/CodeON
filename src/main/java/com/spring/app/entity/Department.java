package com.spring.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_DEPARTMENT")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Department {

    @Id
    @Column(name = "department_seq", nullable = false)
    private Long departmentSeq;         

    @Column(name = "department_name", nullable = false, length = 30)
    private String departmentName;
}

