package com.spring.app.domain;

import java.time.LocalDateTime;

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
public class DraftLineDTO {
	
	   private Long draftLineSeq;
	    private Long fkDraftSeq;
	    private Long fkMemberSeq;
	    private Integer lineOrder; 
	    private Integer signStatus; 
	    private String signComment; 
	    private LocalDateTime signDate; 
    
}
