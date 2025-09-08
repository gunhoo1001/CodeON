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
public class DraftDTO {

    private int draftSeq;
    private int fkDraftTypeSeq;
    private int fkMemberSeq;
    private String draftTitle;
    private String draftContent;
    private int draftStatus;
    private int isEmergency;
    private String draftRegdate;
    
}
