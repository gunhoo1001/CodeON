package com.spring.app.board.domain;

import java.util.Date;

import lombok.Data;

@Data
public class CommentDTO {
	
    private int commentSeq;       // 댓글 PK
    private int fkBoardSeq;       // 게시글 번호
    private int fkMemberSeq;      // 회원 번호
    private String memberName;    // 작성자 이름
    private String commentContent;// 내용
    private Date commentRegdate;  // 작성일
    
    private boolean mine; // 본인이 작성한 댓글인지 확인하는 boolean
    
}
