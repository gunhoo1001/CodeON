package com.spring.app.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spring.app.entity.Signline;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignlineDTO {
	
    private Long signlineSeq;     // PK
    private Long fkMemberSeq;     // 소유자(로그인 사용자) PK - int 쓰면 Integer
    private String signlineName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regdate;

    @Singular
    private List<SignlineMemberDTO> members;
    
    
    // 한 결재라인에 있는 사람 수
    private long memberCount;

}