package com.spring.app.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DraftFileDTO {

	private int draftFileSeq;
	private int fkDraftSeq;
	private String fileName;
	private String filePath;
    
}
