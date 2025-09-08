package com.spring.app.board.model;

import java.util.List;
import java.util.Map;

import com.spring.app.board.domain.BoardDTO;

public interface BoardDAO {
	
	void insertBoard(BoardDTO boardDto);

   

	List<BoardDTO> selectBoardList(Map<String, String> paramMap);

	List<Map<String, Object>> getBoardTypeList();

	List<Map<String, Object>> getBoardCategoryList();


	int getTotalCount(Map<String, Object> paraMap);

	List<BoardDTO> boardListSearch_withPaging(Map<String, Object> paraMap);



	BoardDTO getBoardDetail(String boardSeq);

	void updateReadCount(String boardSeq); //조회수 증가

	BoardDTO getPrevBoard(Map<String, Object> paraMap);


	BoardDTO getNextBoard(Map<String, Object> paraMap);



	int delete(String boardSeq);

	 void updateBoard(BoardDTO boardDto)throws Exception ;

	// 메인 화면 공지사항 최근 n개
	List<BoardDTO> selectRecentNoticesFromTypes(Map<String, Object> p);

	 String getDepartmentNameBySeq(Integer fkDepartmentSeq);


	 //이번주 인기글 
	 List<BoardDTO> selectWeeklyPopularBoard(Map<String, Object> paraMap);



	 List<BoardDTO> selectRecentNotices(Map<String, Object> p);









}