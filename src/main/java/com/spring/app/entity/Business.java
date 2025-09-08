// com.spring.app.entity.BusinessTrip.java
package com.spring.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "TBL_BUSINESS")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Business {
    @Id
    @Column(name = "draft_seq")
    private Long draftSeq;

    @Column(name = "business_title", nullable = false, length = 100)
    private String businessTitle;

    @Lob
    @Column(name="BUSINESS_CONTENT", columnDefinition="CLOB")
    private String businessContent;

    @Column(name = "business_start")
    private LocalDate businessStart;

    @Column(name = "business_end")
    private LocalDate businessEnd;

    @Column(name = "business_location", length = 100)
    private String businessLocation;

    @Column(name = "business_result", length = 500)
    private String businessResult;
}
