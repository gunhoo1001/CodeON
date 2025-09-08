package com.spring.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_GRADE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GRADE_GENERATOR")
    @SequenceGenerator(
        name = "SEQ_GRADE_GENERATOR",
        sequenceName = "seq_grade",
        allocationSize = 1
    )
    @Column(name = "grade_seq", nullable = false)
    private int gradeSeq;

    @Column(name = "grade_name", nullable = false, length = 30)
    private String gradeName;
}
