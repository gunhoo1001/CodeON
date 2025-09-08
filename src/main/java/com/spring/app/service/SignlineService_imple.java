package com.spring.app.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.app.domain.SignlineDTO;
import com.spring.app.domain.SignlineMemberDTO;
import com.spring.app.entity.Department;
import com.spring.app.entity.Grade;
import com.spring.app.entity.Member;
import com.spring.app.entity.Signline;
import com.spring.app.entity.SignlineMember;
import com.spring.app.model.SignlineRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignlineService_imple implements SignlineService {
	
	private final SignlineRepository signlineRepository;

	@Override
    @Transactional(readOnly = true)
    public List<SignlineDTO> findAllByOwner(int memberSeq) {
        return getLinesWithMembers((long) memberSeq);
    }

	@Override
	public List<SignlineDTO> getLinesWithMembers(Long fkMemberSeq) {
		List<Signline> lines = signlineRepository.findAllWithMembersByFkMemberSeq(fkMemberSeq);
        List<SignlineDTO> out = new ArrayList<>(lines.size());

        for (Signline sl : lines) {
            // line_order 정렬
            List<SignlineMember> src = sl.getMembers() == null
                    ? Collections.emptyList()
                    : new ArrayList<>(sl.getMembers());
            src.sort(Comparator.comparingInt(sm -> sm.getLineOrder() == null ? Integer.MAX_VALUE : sm.getLineOrder()));

            // 멤버 DTO 변환
            List<SignlineMemberDTO> memberDtos = new ArrayList<>(src.size());
            for (SignlineMember sm : src) {
                Member m = sm.getMember();
                Department d = (m != null) ? m.getDepartment() : null;
                Grade g = (m != null) ? m.getGrade() : null;

                memberDtos.add(SignlineMemberDTO.builder()
                        .lineOrder(sm.getLineOrder())
                        .memberSeq(
                                m != null
                                    ? (long) m.getMemberSeq()                                
                                    : (sm.getFkMemberSeq() != null ? sm.getFkMemberSeq().longValue() : null) 
                            )
                        .memberName(m != null ? m.getMemberName() : null)
                        .deptName(d != null ? d.getDepartmentName() : null) 
                        .title(g != null ? g.getGradeName() : null)
                        .build());
            }

            out.add(SignlineDTO.builder()
                    .signlineSeq(sl.getSignlineSeq())
                    .fkMemberSeq(sl.getFkMemberSeq())
                    .signlineName(sl.getSignlineName())
                    .regdate(sl.getRegdate())
                    .members(memberDtos)
                    .memberCount(memberDtos.size())
                    .build());
        }
        return out;
	}

	@Override
	public Signline getLineWithMembers(Long signlineSeq) {
		return signlineRepository.findById(signlineSeq)
		        .orElseThrow(() -> new EntityNotFoundException("signline not found"));
	}


}