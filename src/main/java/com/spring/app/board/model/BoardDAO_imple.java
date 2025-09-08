package com.spring.app.board.model;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.spring.app.board.domain.BoardDTO;

import lombok.RequiredArgsConstructor;


@Repository
@RequiredArgsConstructor
public class BoardDAO_imple implements BoardDAO {
	

	 private final SqlSession sqlSession;

	    @Override
	    public void insertBoard(BoardDTO boardDto) {
	        sqlSession.insert("board.insertBoard", boardDto);
	    }

		@Override
		public List<BoardDTO> selectBoardList(Map<String, String> paramMap) {
			 return sqlSession.selectList("board.selectBoardList", paramMap);
		}

		@Override
		public List<Map<String, Object>> getBoardTypeList() {
			return sqlSession.selectList("board.getBoardTypeList");
		}

		@Override
		public List<Map<String, Object>> getBoardCategoryList() {
			return sqlSession.selectList("board.getBoardCategoryList");
		}

		@Override
		public int getTotalCount(Map<String, Object> paraMap) {
			return sqlSession.selectOne("board.getTotalCount",paraMap);
		}

		@Override
		public List<BoardDTO> boardListSearch_withPaging(Map<String, Object> paraMap) {
			return sqlSession.selectList("board.boardListSearch_withPaging",paraMap);
		}

		@Override
	    public BoardDTO getBoardDetail(String boardSeq) {
	        return sqlSession.selectOne("board.getBoardDetail", boardSeq);
	    }

		@Override
		public void updateReadCount(String boardSeq) {
			sqlSession.update("board.updateReadCount", boardSeq);
			
		}
		
	  
		// 상세페이지 이전글 다음글 
		@Override
		public BoardDTO getPrevBoard(Map<String, Object> paraMap) {
		    return sqlSession.selectOne("board.getPrevBoard", paraMap);
		}

	
		@Override
		public BoardDTO getNextBoard(Map<String, Object> paraMap) {
		    return sqlSession.selectOne("board.getNextBoard", paraMap);
		}
		

		@Override
		public int delete(String boardSeq) {
			return sqlSession.delete("board.delete", boardSeq);
		}

		@Override
		public void updateBoard(BoardDTO boardDto) throws Exception {
			sqlSession.update("board.updateBoard", boardDto);
			
		}

		@Override
		public List<BoardDTO> selectRecentNoticesFromTypes(Map<String, Object> p) {
			return sqlSession.selectList("board.selectRecentNoticesFromTypes", p);
		}
		
	     @Override
		    public String getDepartmentNameBySeq(Integer fkDepartmentSeq) {
		        return sqlSession.selectOne("getDepartmentNameBySeq", fkDepartmentSeq);
		    }

		  @Override
		  public List<BoardDTO> selectWeeklyPopularBoard(Map<String, Object> paraMap) {
			  return sqlSession.selectList("board.selectWeeklyPopularBoard", paraMap);
		  }

		  
		  // 최근공지 불러오기
		  @Override
		  public List<BoardDTO> selectRecentNotices(Map<String, Object> p) {
		      return sqlSession.selectList("board.selectRecentNotices", p);
		  }
		

	


}