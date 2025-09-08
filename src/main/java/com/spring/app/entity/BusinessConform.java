// com.spring.app.entity.BusinessConform.java
package com.spring.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_BUSINESS_CONFORM")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BusinessConform {
    @Id
    @Column(name = "draft_seq")
    private Long draftSeq;

    @Column(name = "conform_title", nullable = false, length = 100)
    private String conformTitle;

    @Lob
    @Column(name="CONFORM_CONTENT", columnDefinition="CLOB")
    private String conformContent;
}
