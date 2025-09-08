package com.spring.app.board.model;

import java.util.List;
import java.util.Map;

import com.spring.app.board.domain.CommentDTO;
import com.spring.app.board.domain.ReplyDTO;

public interface CommentDAO {
	
	 
		int insertComment(Map<String, Object> paramMap);
	
	    List<CommentDTO> selectCommentList(int fkBoardSeq);

		

		int insertReply(Map<String, Object> paramMap);

		List<ReplyDTO> selectReplyList(Integer parentCommentSeq);

		int deleteComment(Integer commentSeq, int fkMemberSeq);

		int deleteReply(Integer replySeq, int fkMemberSeq);

		int updateComment(CommentDTO cdto);

		int updateReply(ReplyDTO rdto);

		int insertRecommend(Map<String, Object> paramMap);

		int removeRecommend(Map<String, Object> paramMap);
		
		int selectRecommendCount(Integer fkBoardSeq);

		List<String> selectRecommendMemberNames(Integer fkBoardSeq);

		int existsRecommend(Map<String, Object> paramMap);

	

		
	    
}
