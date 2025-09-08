package com.spring.app.board.domain;

import java.util.Date;

import lombok.Data;


@Data
public class ReplyDTO {
		private int replySeq;          // 대댓글 PK
	    private int fkCommentSeq;      // 부모 댓글 번호
	    private int fkMemberSeq;       // 회원 번호
	    private String memberName;     // 작성자 이름 (조인용)
	    private String replyContent;   // 대댓글 내용
	    private Date replyRegdate;     // 작성일
	    private boolean mine; // 본인이 작성한 대댓글인지 확인하는 boolean
}
