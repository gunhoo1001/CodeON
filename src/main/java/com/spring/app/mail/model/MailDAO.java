package com.spring.app.mail.model;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.spring.app.mail.domain.MailDTO;
import com.spring.app.mail.domain.MailUserStatusDTO;

@Mapper
public interface MailDAO {

	// 시퀀스 가져오기
	long getEmailSeq();
	
	// 수신자/발신자 상태 저장
	int insertMailUser(MailDTO mailDto);
	
	// 파일첨부가 없는 글쓰기
	void insertMail(MailDTO mailDto);
	
	// 파일첨부가 있는 글쓰기 
	void insertMail_withFile(MailDTO mailDto);

	// 총 메일 (totalCount) 구하기
	int getTotalCount(Map<String, String> paraMap);

	// 메일목록 가져오기
	List<MailDTO> mailListSearch_withPaging(Map<String, String> paraMap);
	
	// 별 업데이트
	int updateImportant(Map<String, String> params);

	// 읽음 업데이트
	int updateReadStatus(Map<String, String> paraMap);

	// 메일 select
	MailDTO selectOne(String emailSeq);

	// 읽은 메일 개수
	String getCount(String loginUserEmail);

	// 총 메일 개수 구하기
	String totalCount(String loginUserEmail);

	// 메일 여러개 삭제하기
	int deleteMails(List<Long> emailSeqList);

	// 메일 하나 삭제하기
	int deleteMail(String emailSeq);
	
	int deleteByEmailSeqList(List<Long> emailSeqList);

	int getSentMailTotalCount(Map<String, String> paraMap);

	List<MailDTO> getSentMailListWithPaging(Map<String, String> paraMap);

	int getReceivedMailTotalCount(Map<String, String> paraMap);

	List<MailDTO> getReceivedMailListWithPaging(Map<String, String> paraMap);

	List<MailUserStatusDTO> MailUserList(String valueOf);
	
	//스케줄러
	List<MailUserStatusDTO> selectScheduledUsers();

	int deleteByEmailSeq(String emailSeq);

}
