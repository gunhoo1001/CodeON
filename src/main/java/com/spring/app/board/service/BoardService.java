package com.spring.app.board.service;

import com.spring.app.board.domain.BoardDTO;

import java.util.List;
import java.util.Map;

public interface BoardService {
    void add(BoardDTO boardDto) throws Exception;

   
	List<BoardDTO> selectBoardList(Map<String, String> paramMap);


	List<Map<String, Object>> getBoardTypeList();


	List<Map<String, Object>> getBoardCategoryList();


	int getTotalCount(Map<String, Object> paraMap);

	
	List<BoardDTO> boardListSearch_withPaging(Map<String, Object> paraMap);


	BoardDTO getBoardDetail(String boardSeq);


	BoardDTO getPrevBoard(Map<String, Object> paraMap);


	BoardDTO getNextBoard(Map<String, Object> paraMap);

	//글 삭제
	 int delete(String boardSeq);


	 void updateBoard(BoardDTO boardDto)throws Exception;


	 String getDepartmentNameBySeq(Integer userDept);

	
	 // 메인 화면
	 List<BoardDTO> selectRecentNotices(int userDeptSeq, int limit);


	 List<BoardDTO> getWeeklyPopularBoard(int fkBoardTypeSeq, Integer fkDepartmentSeq);





	 


}