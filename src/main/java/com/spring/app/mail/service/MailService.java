package com.spring.app.mail.service;

import java.util.List;
import java.util.Map;

import com.spring.app.mail.domain.MailDTO;
import com.spring.app.mail.domain.MailUserStatusDTO;

public interface MailService {

	// 파일첨부가 없는 글쓰기
	int write(MailDTO mailDto);

	// 파일첨부가 있는 글쓰기 
	int write_withFile(MailDTO mailDto);

	// 총 메일 (totalCount) 구하기
	int getTotalCount(Map<String, String> paraMap);

	// 메일목록 가져오기
	List<MailDTO> mailListSearch_withPaging(Map<String, String> paraMap);

	// 중요 업데이트
	int updateImportant(Map<String, String> paraMap);

	// 읽음 업데이트
	int updateReadStatus(Map<String, String> paraMap);

	// 메일 select
	MailDTO selectOne(String emailSeq);

	// 읽은 메일 개수 구하기
	String getCount(String loginUserEmail);

	// 총 메일 개수 구하기
	String getTotalCount(String loginUserEmail);

	// 메일 여러개 삭제하기
	int deleteMails(List<Long> emailSeqList);

	// 메일 하나 삭제하기
	int deleteMail(String emailSeq);
	
	int deleteByEmailSeqList(List<Long> emailSeqList);

	// 보낸 메일 총 개수
	int getSentMailTotalCount(Map<String, String> paraMap);

	// 보낸 메일목록 가져오기
	List<MailDTO> getSentMailListWithPaging(Map<String, String> paraMap);

	// 받은 메일 총 개수
	int getReceivedMailTotalCount(Map<String, String> paraMap);

	// 받은 메일목록 가져오기
	List<MailDTO> getReceivedMailListWithPaging(Map<String, String> paraMap);

	List<MailUserStatusDTO> MailUserList();

	List<MailUserStatusDTO> getScheduledUsers();
	
	int deleteByEmailSeq(String emailSeq);

}
