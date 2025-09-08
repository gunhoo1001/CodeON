package com.spring.app.mail.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.spring.app.board.domain.BoardDTO;
import com.spring.app.board.service.BoardService;
import com.spring.app.mail.domain.MailDTO;
import com.spring.app.mail.domain.MailUserStatusDTO;
import com.spring.app.mail.service.MailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MailScheduler {

    private final MailService mailService;
    private final BoardService boardService;
    
    
    @Scheduled(cron = "0 0 * * * *") //한시간 단위로
    public void sendScheduledMail() {
        int fkBoardTypeSeq = 0; // 예: 사내게시판
        Integer fkDepartmentSeq = null; // 전체
        
        // DB에서인기글 조회
        List<BoardDTO> popularBoards = boardService.getWeeklyPopularBoard(fkBoardTypeSeq, fkDepartmentSeq);

        // 메일 내용 구성
     // 메일 내용 구성 (HTML)
        StringBuilder content = new StringBuilder();
        content.append("<h2>📌 실시간 인기글 TOP5</h2>");
        content.append("<table style='border-collapse:collapse; width:100%;'>");
        content.append("<thead><tr style='background:#f3f4f6;'>")
               .append("<th style='border:1px solid #ddd; padding:8px;'>순위</th>")
               .append("<th style='border:1px solid #ddd; padding:8px;'>제목</th>")
               .append("<th style='border:1px solid #ddd; padding:8px;'>조회수</th>")
               .append("<th style='border:1px solid #ddd; padding:8px;'>추천수</th>")
               .append("</tr></thead><tbody>");

        int rank = 1;
        for (BoardDTO b : popularBoards) {
            content.append("<tr>")
                   .append("<td style='border:1px solid #ddd; padding:8px; text-align:center;'>").append(rank++).append("</td>")
                   .append("<td style='border:1px solid #ddd; padding:8px;'>").append(b.getBoardTitle()).append("</td>")
                   .append("<td style='border:1px solid #ddd; padding:8px; text-align:center;'>").append(b.getBoardReadcount()).append("</td>")
                   .append("<td style='border:1px solid #ddd; padding:8px; text-align:center;'>").append(b.getRecommendCount()).append("</td>")
                   .append("</tr>");
        }

        content.append("</tbody></table>");
        content.append("<p style='margin-top:16px; color:#6b7280;'>※ 본 메일은 시스템에서 자동 발송되었습니다.</p>");


        // MailDTO 구성
        MailDTO mail = MailDTO.builder()
                .sendMemberEmail("park@CodeON.com")
                .receiveMemberEmail("leess@CodeON.com")
                .emailTitle("실시간 인기글 현황(관리자용)")
                .emailContent(content.toString())
                .build();

        // 수신자/발신자 상태 설정
        List<MailUserStatusDTO> statusList = new ArrayList<>();
        statusList.add(MailUserStatusDTO.builder()
                .memberEmail(mail.getSendMemberEmail())
                .readStatus("1")
                .importantStatus("0")
                .build());
        for (String rEmail : mail.getReceiveMemberEmail().split(",")) {
            if (rEmail.isEmpty()) continue;
            statusList.add(MailUserStatusDTO.builder()
                    .memberEmail(rEmail)
                    .readStatus("0")
                    .importantStatus("0")
                    .build());
        }
        mail.setUserStatusList(statusList);

        // 메일 DB 저장
        mailService.write(mail);

        System.out.println("자동 발송 메일 완료: " + mail.getEmailTitle());
    }
}
