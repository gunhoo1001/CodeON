package com.spring.app.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.app.entity.DraftFile;

public interface DraftFileRepository extends JpaRepository<DraftFile, Long> {
	List<DraftFile> findByDraft_DraftSeqOrderByDraftFileSeqAsc(Long draftSeq);
}
