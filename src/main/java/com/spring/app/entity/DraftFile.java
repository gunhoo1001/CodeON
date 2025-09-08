package com.spring.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_DRAFT_FILE")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DraftFile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DRAFT_FILE_GEN")
    @SequenceGenerator(name = "SEQ_DRAFT_FILE_GEN", sequenceName = "SEQ_DRAFT_FILE", allocationSize = 1)
    @Column(name = "draft_file_seq", nullable = false)
    private Long draftFileSeq;

    // FK: TBL_DRAFT(draft_seq)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_draft_seq", nullable = false)
    private Draft draft;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;
}