package com.spring.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_DRAFT_TYPE")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DraftType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DRAFT_TYPE_GEN")
    @SequenceGenerator(name = "SEQ_DRAFT_TYPE_GEN", sequenceName = "SEQ_DRAFT_TYPE", allocationSize = 1)
    @Column(name = "draft_type_seq", nullable = false)
    private Long draftTypeSeq;

    @Column(name = "draft_type_name", nullable = false, length = 255)
    private String draftTypeName;
}
