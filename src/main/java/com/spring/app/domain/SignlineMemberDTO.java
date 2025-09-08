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
public class SignlineMemberDTO {
	
    private Long signlineMemberSeq;
    private Long signlineSeq;
    private Long memberSeq;
    private Integer lineOrder;

    // 조인 결과
    private String memberName;
    private String deptName;
    private String title;
    
}