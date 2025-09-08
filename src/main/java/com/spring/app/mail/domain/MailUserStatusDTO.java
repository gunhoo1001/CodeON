package com.spring.app.mail.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailUserStatusDTO {

    private String emailSeq;            // 메일 고유번호 (MailDTO와 연결)
    private String memberEmail;       // 사용자 이메일 (수신자)
    private String readStatus;       // 읽음 상태 (0: 안읽음, 1: 읽음)
    private String importantStatus;    // 중요 표시
}
