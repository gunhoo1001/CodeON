// com.spring.app.entity.Vacation.java
package com.spring.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "TBL_VACATION")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vacation {
    @Id
    @Column(name = "draft_seq")
    private Long draftSeq;

    @Column(name = "vacation_title", nullable = false, length = 100)
    private String vacationTitle;

    @Column(name = "vacation_type", nullable = false, length = 100)
    private String vacationType; // 화면에 없으니 우선 "기타" 같은 기본값으로 저장

    @Column(name = "vacation_start", nullable = false)
    private LocalDate vacationStart;

    @Column(name = "vacation_end", nullable = false)
    private LocalDate vacationEnd;

    @Lob
    @Column(name="VACATION_CONTENT", columnDefinition="CLOB")
    private String vacationContent;
}
