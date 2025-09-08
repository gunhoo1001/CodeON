package com.spring.app.mail.domain;

import lombok.*;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailDTO {

    // --- 메일 정보 ---
    private String emailSeq;
    private String sendMemberEmail;
    private String receiveMemberEmail;
    private String emailTitle;
    private String emailContent;
    private String emailRegdate;
    
    private MultipartFile attach;
    
    private String emailFilename;
    private String emailOrgFilename;
    private String emailFilesize;

    // --- 사용자별 상태 ---
    private List<MailUserStatusDTO> userStatusList;
    
    private String readStatus;
    private String importantStatus;
}


