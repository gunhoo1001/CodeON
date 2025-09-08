// src/main/java/com/spring/app/entity/Attendance.java
package com.spring.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_ATTENDANCE")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ATTENDANCE")
    @SequenceGenerator(name = "SEQ_ATTENDANCE", sequenceName = "SEQ_ATTENDANCE", allocationSize = 1)
    @Column(name = "ATTENDANCE_SEQ", nullable = false)
    private Long attendanceSeq;

    @Column(name = "FK_MEMBER_SEQ", nullable = false)
    private Integer memberSeq;

    @Column(name = "WORK_DATE", nullable = false)
    private LocalDate workDate;

    // Oracle TIMESTAMP(6) → LocalDateTime 매핑
    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    @Column(name = "END_TIME")
    private LocalDateTime endTime;

    // OVERTIME NUMBER → 분 단위 정수로 사용 (정책에 맞게 시간/분 환산)
    @Column(name = "OVERTIME")
    private Integer overtime; // minutes
}
