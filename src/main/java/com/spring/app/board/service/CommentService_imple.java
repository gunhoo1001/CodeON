package com.spring.app.board.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.spring.app.board.domain.CommentDTO;
import com.spring.app.board.domain.ReplyDTO;
import com.spring.app.board.model.CommentDAO;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class CommentService_imple implements CommentService {

	private final CommentDAO dao;
	
	
	@Override
	public List<CommentDTO> getCommentList(int fkBoardSeq) {
		return dao.selectCommentList(fkBoardSeq);
	}

	@Override
    public int addComment(Map<String, Object> paramMap) {
        return dao.insertComment(paramMap);
    }

	@Override
	public int addReply(Map<String, Object> paramMap) {
		return dao.insertReply(paramMap);
	}

	@Override
	public List<ReplyDTO> getReplyList(Integer parentCommentSeq) {
		 return dao.selectReplyList(parentCommentSeq);
	}

	@Override
    public int deleteComment(Integer commentSeq, int memberSeq) {
        return dao.deleteComment(commentSeq, memberSeq);
    }

	 @Override
	    public int deleteReply(Integer replySeq, int memberSeq) {
	        // 본인 대댓글만 삭제
	        return dao.deleteReply(replySeq, memberSeq);
	    }

	 @Override
	 public int editComment(CommentDTO cdto) {
		 return dao.updateComment(cdto);
	 }

	 @Override
	 public int editReply(ReplyDTO rdto) {
		  return dao.updateReply(rdto);
	 }

	 
	 @Override
	 public int addRecommend(Map<String, Object> paramMap) {
		 return dao.insertRecommend(paramMap);
	 }
	
	 @Override
	 public boolean existsRecommend(Map<String, Object> paramMap) {
		 int count = dao.existsRecommend(paramMap);
	        return count > 0;
	 }

	 @Override
	 public int removeRecommend(Map<String, Object> paramMap) {
		 return dao.removeRecommend(paramMap);
	 }
	 
	 @Override
	 public int getRecommendCount(Integer fkBoardSeq) {
		  return dao.selectRecommendCount(fkBoardSeq);
	 }
	 
	 @Override
	 public List<String> getRecommendMemberNames(Integer fkBoardSeq) {
		 return dao.selectRecommendMemberNames(fkBoardSeq);
	 }

	 
	 
}
