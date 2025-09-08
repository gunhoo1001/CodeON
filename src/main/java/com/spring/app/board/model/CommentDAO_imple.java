package com.spring.app.board.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.spring.app.board.domain.CommentDTO;
import com.spring.app.board.domain.ReplyDTO;

import lombok.RequiredArgsConstructor;
@Repository
@RequiredArgsConstructor

public class CommentDAO_imple implements CommentDAO {

	private final SqlSession sqlSession;
	
	

	    @Override
	    public List<CommentDTO> selectCommentList(int fkBoardSeq) {
	        return sqlSession.selectList("comment.selectCommentList", fkBoardSeq);
	    }



	    @Override
	    public int insertComment(Map<String, Object> paramMap) {
	        return sqlSession.insert("comment.insertComment", paramMap);
	    }



		@Override
		public int insertReply(Map<String, Object> paramMap) {
			return sqlSession.insert("comment.insertReply", paramMap);
		}



		@Override
		public List<ReplyDTO> selectReplyList(Integer parentCommentSeq) {
			return sqlSession.selectList("comment.selectReplyList", parentCommentSeq);
		}



		@Override
		public int deleteComment(Integer commentSeq, int fkMemberSeq) {
		    Map<String, Object> paramMap = new HashMap<>();
		    paramMap.put("commentSeq", commentSeq);
		    paramMap.put("fkMemberSeq", fkMemberSeq);
		    return sqlSession.delete("comment.deleteComment", paramMap);
		}



		@Override
		public int deleteReply(Integer replySeq, int fkMemberSeq) {
			  Map<String, Object> paramMap = new HashMap<>();
			    paramMap.put("replySeq", replySeq);
			    paramMap.put("fkMemberSeq", fkMemberSeq);
			    return sqlSession.delete("comment.deleteReply", paramMap);
		}



		@Override
		public int updateComment(CommentDTO cdto) {
			 return sqlSession.update("comment.updateComment", cdto);
		}



		@Override
		public int updateReply(ReplyDTO rdto) {
			 return sqlSession.update("comment.updateReply", rdto);
		}



		@Override
	    public int insertRecommend(Map<String, Object> paramMap) {
	        return sqlSession.insert("comment.insertRecommend", paramMap);
	    }

	    @Override
	    public int selectRecommendCount(Integer fkBoardSeq) {
	        return sqlSession.selectOne("comment.selectRecommendCount", fkBoardSeq);
	    }


	    //추천한 사람 이름 조회
		@Override
		public List<String> selectRecommendMemberNames(Integer fkBoardSeq) {
			return sqlSession.selectList("comment.selectRecommendMemberNames", fkBoardSeq);
		}



		@Override
		public int removeRecommend(Map<String, Object> paramMap) {
			 return sqlSession.delete("comment.deleteRecommend", paramMap);
		}



		@Override
		public int existsRecommend(Map<String, Object> paramMap) {
			  return sqlSession.selectOne("comment.existsRecommend", paramMap);
		}
		
		
		
}
