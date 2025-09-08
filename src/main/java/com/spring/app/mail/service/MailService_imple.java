package com.spring.app.mail.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.app.mail.domain.MailDTO;
import com.spring.app.mail.domain.MailUserStatusDTO;
import com.spring.app.mail.model.MailDAO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService_imple implements MailService {
	
	private final MailDAO dao;

	
	// 파일첨부가 없는 글쓰기
	@Override
	@Transactional
	public int write(MailDTO mailDto) {
		
		int n = 0;
		
	    // 1. 시퀀스 가져오기
	    long emailSeq = dao.getEmailSeq();
	    mailDto.setEmailSeq(String.valueOf(emailSeq));
	    
	    if(mailDto.getUserStatusList() != null) {
	        for(MailUserStatusDTO status : mailDto.getUserStatusList()) {
	            status.setEmailSeq(String.valueOf(emailSeq));
	        }
	    }

	    // 2. 메일 저장
	    dao.insertMail(mailDto);

	    // 3. 수신자/발신자 상태 저장
	    if(mailDto.getUserStatusList() != null && !mailDto.getUserStatusList().isEmpty()) {
	    	dao.insertMailUser(mailDto);
	    	n = 1;
	    }

	    return n;
	}

	// 파일첨부가 있는 글쓰기 
	@Override
	public int write_withFile(MailDTO mailDto) {
		
		int n = 0;
		
	    // 1. 시퀀스 가져오기
	    long emailSeq = dao.getEmailSeq();
	    mailDto.setEmailSeq(String.valueOf(emailSeq));
	    
	    if(mailDto.getUserStatusList() != null) {
	        for(MailUserStatusDTO status : mailDto.getUserStatusList()) {
	            status.setEmailSeq(String.valueOf(emailSeq));
	        }
	    }

	    // 2. 메일 저장
	    dao.insertMail_withFile(mailDto);

	    // 3. 수신자 상태 저장
	    if(mailDto.getUserStatusList() != null && !mailDto.getUserStatusList().isEmpty()) {
	    	dao.insertMailUser(mailDto);
	    	n = 1;
	    }

	    return n;
	}

	// 총 메일 (totalCount) 구하기
	@Override
	public int getTotalCount(Map<String, String> paraMap) {
		int totalCount = dao.getTotalCount(paraMap);
		return totalCount;
	}

	// 메일목록 가져오기
	@Override
	public List<MailDTO> mailListSearch_withPaging(Map<String, String> paraMap) {
		return dao.mailListSearch_withPaging(paraMap);
		
	}
	
	@Override
	public List<MailUserStatusDTO> MailUserList() {
		long emailSeq = dao.getEmailSeq();
		
		return dao.MailUserList(String.valueOf(emailSeq));
	}


	// 별 업데이트
	@Override
	public int updateImportant(Map<String, String> paraMap) {
		return dao.updateImportant(paraMap);
	}

	// 읽음 업데이트
	@Override
	public int updateReadStatus(Map<String, String> paraMap) {
		return dao.updateReadStatus(paraMap);
	}

	// 메일 select
	@Override
	public MailDTO selectOne(String emailSeq) {
		return dao.selectOne(emailSeq);
	}

	// 읽은 메일 개수
	@Override
	public String getCount(String loginUserEmail) {
		return dao.getCount(loginUserEmail);
	}

	// 총 메일 개수 구하기
	@Override
	public String getTotalCount(String loginUserEmail) {
		return dao.totalCount(loginUserEmail);
	}

	// 메일 여러개 삭제하기
	@Override
	public int deleteMails(List<Long> emailSeqList) {
		return dao.deleteMails(emailSeqList);
	}

	// 메일 하나 삭제하기
	@Override
	public int deleteMail(String emailSeq) {
		return dao.deleteMail(emailSeq);
	}
	
	@Override
	public int deleteByEmailSeqList(List<Long> emailSeqList) {
		return dao.deleteByEmailSeqList(emailSeqList);
	}

	@Override
	public int getSentMailTotalCount(Map<String, String> paraMap) {
		return dao.getSentMailTotalCount(paraMap);
	}

	@Override
	public List<MailDTO> getSentMailListWithPaging(Map<String, String> paraMap) {
		return dao.getSentMailListWithPaging(paraMap);
	}

	@Override
	public int getReceivedMailTotalCount(Map<String, String> paraMap) {
		return dao.getReceivedMailTotalCount(paraMap);
	}

	@Override
	public List<MailDTO> getReceivedMailListWithPaging(Map<String, String> paraMap) {
		return dao.getReceivedMailListWithPaging(paraMap);
	}

	@Override
    public List<MailUserStatusDTO> getScheduledUsers() {
        return dao.selectScheduledUsers();
    }
	
	@Override
	public int deleteByEmailSeq(String emailSeq) {
		return dao.deleteByEmailSeq(emailSeq);
		
	}


}
