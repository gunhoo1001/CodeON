package com.spring.app.service;

import static com.spring.app.entity.QDepartment.department;
import static com.spring.app.entity.QGrade.grade;
import static com.spring.app.entity.QMember.member;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.app.domain.MemberDTO;
import com.spring.app.entity.AnnualLeave;
import com.spring.app.entity.Member;
import com.spring.app.model.MemberDAO;
import com.spring.app.model.MemberRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService_imple implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberDAO mbrdao;
    private final JPAQueryFactory jPAQueryFactory;

    @PersistenceContext
    private EntityManager em;

    // 사용자 ID로 회원 정보 조회
    @Override
    public MemberDTO getMemberByUserId(String memberUserId) {

        MemberDTO mbrDto = null;

        try {
            Optional<Member> memberOpt = memberRepository.findByMemberUserid(memberUserId);

            Member mbr = memberOpt.get(); // 없으면 NoSuchElementException 발생
            mbrDto = mbr.toDTO();

        } catch (NoSuchElementException e) {
            // 로그인 실패 시 null 리턴
        }

        return mbrDto;
    }

    // 직원등록
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Member registerMember(Member member) {

        // === 1. 사번 생성 === //
        String yearStr = String.valueOf(member.getMemberHiredate().getYear());
        String deptStr = String.format("%02d", member.getFkDepartmentSeq());

        // 시퀀스에서 다음 값 가져오기
        Integer seq = ((Number) em.createNativeQuery("SELECT MEMBER_SEQ_GENERATOR.NEXTVAL FROM DUAL")
                .getSingleResult()).intValue();

        String seqStr = String.format("%03d", seq);
        String memberSeqStr = yearStr + deptStr + seqStr;
        int memberSeqInt = Integer.parseInt(memberSeqStr);

        member.setMemberSeq(memberSeqInt);
        member.setMemberUserid(member.getMemberUserid() + memberSeqStr);
        member.setMemberPwd(member.getMemberPwd() + memberSeqStr);
        member.setMemberEmail(member.getMemberEmail() + "@CodeON.com");

        // === 2. 연차 생성 === //
        LocalDate now = LocalDate.now();
        LocalDate hire = member.getMemberHiredate();
        int totalMonth = (now.getYear() - hire.getYear()) * 12 + (now.getMonthValue() - hire.getMonthValue());

        AnnualLeave annualLeave = AnnualLeave.builder()
                .totalLeave(totalMonth)
                .usedLeave(0)
                .remainingLeave(totalMonth)
                .member(member) // 연차 -> Member 연관 설정
                .build();

        // === 3. Member에 AnnualLeave 매핑 === //
        member.setAnnualLeave(annualLeave); // Member -> 연차 매핑

        // === 4. Member 저장 (Cascade로 AnnualLeave도 함께 저장) === //
        return memberRepository.save(member);
    }



    
    // 직원수정
    @Override
    @Transactional
    public Member updateMember(Member member) {

        return memberRepository.save(member);
    }

	@Override
	public Page<Member> getPageMember(String searchType, String searchWord, String gender, int currentShowPageNo, int sizePerPage) throws Exception {
		
		Page<Member> page = Page.empty();
		
		try {

			Pageable pageable = PageRequest.of(currentShowPageNo - 1, sizePerPage, Sort.by(Sort.Direction.DESC, "memberHiredate"));
			
	         BooleanExpression condition = Expressions.TRUE; 
	         
	 		if ("fkDepartmentSeq".equals(searchType) && (searchWord != null && !searchWord.trim().isEmpty())) {
			    // 부서명으로 조건 걸기 (조인)
			    condition = condition.and(department.departmentName.contains(searchWord));
			
			} else if ("memberName".equals(searchType) && (searchWord != null && !searchWord.trim().isEmpty())) {
			    
				condition = condition.and(member.memberName.contains(searchWord));
			
			} else if ("fkGradeSeq".equals(searchType) && (searchWord != null && !searchWord.trim().isEmpty())) {
				
		        condition = condition.and(grade.gradeName.contains(searchWord));
		    }
			
			if ("0".equals(gender) || "1".equals(gender)) {
			    
				condition = condition.and(member.memberGender.eq(Integer.parseInt(gender)));
			
			}

			List<Member> members = jPAQueryFactory
			                        .selectFrom(member)
			                        .join(member.department, department)
			                        .join(member.grade, grade) // 조인
			                        .where(condition)
			   	                 	.offset(pageable.getOffset())
			   	                 	.limit(pageable.getPageSize())
			   	                 	.orderBy(member.memberHiredate.desc())
			                        .fetch();     
			
		    Long total = jPAQueryFactory
		                 .select(member.count())
		                 .from(member)
	                     .join(member.department, department)
	                     .join(member.grade, grade)
		                 .where(condition)
		                 .fetchOne();

	         page = new PageImpl<>(members, pageable, total != null ? total : 0);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return page;
	}

	// 회원 삭제
	@Override
	public int delete(int memberSeq) {
	    int n = 0;
	    try {
	        memberRepository.deleteById(memberSeq);
	        n = 1;
	    } catch (EmptyResultDataAccessException e) {
	        e.printStackTrace();
	    }
	    return n;
	}

	// 직원 찾기
	@Override
	public MemberDTO getMemberOne(String memberSeq) {
		
		int seq = Integer.parseInt(memberSeq);
		
		BooleanExpression condition = member.memberSeq.eq(seq);
		
	    Member mbr = jPAQueryFactory
	                .selectFrom(member)
	                .where(condition)
	                .fetchOne();
	    
	    return mbr.toDTO();
	}
	
	// 검색 회원 조회
	@Override
	public List<MemberDTO> searchMember(Map<String, String> paraMap) {
		List<MemberDTO> memberDtoList = new ArrayList<>();
		
		String searchType = paraMap.get("searchType");
		String searchWord = paraMap.get("searchWord");
		String gender = paraMap.get("gender");
		
		// >>> BooleanExpression은 QueryDSL 에서 제공해주는 클래스 이다.
		// BooleanExpression 클래스는 QueryDSL 전용의 SQL의 WHERE 조건 표현 객체로서 QueryDSL의 .where(), .and(), .or() 에만 사용된다. <<<
		
		BooleanExpression condition = Expressions.TRUE;
		// Expressions.TRUE 라고 준것은 기본 조건 (항상 참)으로 시작해서 조건을 점진적으로 추가한다. 마치 WHERE 1=1 과 같은 뜻이다. 
		
		if ("fkDepartmentSeq".equals(searchType) && (searchWord != null && !searchWord.trim().isEmpty())) {
		    // 부서명으로 조건 걸기 (조인)
		    condition = condition.and(department.departmentName.contains(searchWord));
		
		} else if ("memberName".equals(searchType) && (searchWord != null && !searchWord.trim().isEmpty())) {
		    
			condition = condition.and(member.memberName.contains(searchWord));
		
		} else if ("fkGradeSeq".equals(searchType) && (searchWord != null && !searchWord.trim().isEmpty())) {
			
	        condition = condition.and(grade.gradeName.contains(searchWord));
	    }
		
		if ("0".equals(gender) || "1".equals(gender)) {
		    
			condition = condition.and(member.memberGender.eq(Integer.parseInt(gender)));
		
		}

		List<Member> members = jPAQueryFactory
		                        .selectFrom(member)
		                        .join(member.department, department)
		                        .join(member.grade, grade) // 조인
		                        .where(condition)
		                        .fetch();
		/*
		for (Member mbr : members) {
			memberDtoList.add(mbr.toDTO());
		}
		*/
		
		memberDtoList = members.stream()
							   .map(Member::toDTO)
							   .collect(Collectors.toList());

		return memberDtoList;
	}


    public List<Member> getAllMembersOrderByDept() {
    	return memberRepository.findAllByOrderByFkDepartmentSeqAsc();
    }

    // 결재라인에 추가할 수 있는 직원(사원 제외 전부)
    @Override
	public List<MemberDTO> getSignlineMember() {
		List<MemberDTO> memberDtoList = new ArrayList<>();
		
		List<Member> members = memberRepository.getSignlineMember();
		
		memberDtoList = members.stream().map(Member::toDTO).collect(Collectors.toList());
		
		return memberDtoList;
	}
    
	// Excel 파일로 다운
	@Override
	public void memberList_to_Excel(Map<String, String> paraMap, Model model) {

	    SXSSFWorkbook workbook = new SXSSFWorkbook();
	    SXSSFSheet sheet = workbook.createSheet("CodeON 사원정보");

	    // 열 너비 설정
	    int[] columnWidths = {4000, 4000, 4000, 4000, 4000, 8000};
	    for (int i = 0; i < columnWidths.length; i++) {
	        sheet.setColumnWidth(i, columnWidths[i]);
	    }

	    int rowLocation = 0;

	    // 스타일 설정
	    CellStyle mergeRowStyle = workbook.createCellStyle();
	    mergeRowStyle.setAlignment(HorizontalAlignment.CENTER);
	    mergeRowStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	    mergeRowStyle.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
	    mergeRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

	    Font mergeRowFont = workbook.createFont();
	    mergeRowFont.setFontName("나눔고딕");
	    mergeRowFont.setFontHeightInPoints((short)14);
	    mergeRowFont.setColor(IndexedColors.WHITE.getIndex());
	    mergeRowFont.setBold(true);
	    mergeRowStyle.setFont(mergeRowFont);

	    CellStyle headerStyle = workbook.createCellStyle();
	    headerStyle.setAlignment(HorizontalAlignment.CENTER);
	    headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	    headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
	    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    headerStyle.setBorderTop(BorderStyle.THICK);
	    headerStyle.setBorderBottom(BorderStyle.THICK);
	    headerStyle.setBorderLeft(BorderStyle.THIN);
	    headerStyle.setBorderRight(BorderStyle.THIN);

	    // 머지 행
	    Row mergeRow = sheet.createRow(rowLocation);
	    for (int i = 0; i < 6; i++) {
	        Cell cell = mergeRow.createCell(i);
	        cell.setCellStyle(mergeRowStyle);
	        cell.setCellValue("CodeON 사원정보");
	    }
	    sheet.addMergedRegion(new CellRangeAddress(rowLocation, rowLocation, 0, 5));

	    // 헤더
	    Row headerRow = sheet.createRow(++rowLocation);
	    String[] headers = {"입사일", "사원번호", "부서코드", "직급코드", "이름", "이메일"};
	    for (int i = 0; i < headers.length; i++) {
	        Cell headerCell = headerRow.createCell(i);
	        headerCell.setCellValue(headers[i]);
	        headerCell.setCellStyle(headerStyle);
	    }

	    // 날짜 스타일
	    CellStyle dateCellStyle = workbook.createCellStyle();
	    CreationHelper createHelper = workbook.getCreationHelper();
	    dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));

	    // 데이터
	    List<MemberDTO> memberList = searchMember(paraMap); // 검색 조건 반영
	    for (int i = 0; i < memberList.size(); i++) {
	        MemberDTO member = memberList.get(i);
	        Row bodyRow = sheet.createRow(i + rowLocation + 1);

	        // 입사일
	        Cell cell0 = bodyRow.createCell(0);
	        if(member.getMemberHiredate() != null) {
	            cell0.setCellValue(java.sql.Date.valueOf(member.getMemberHiredate()));
	            cell0.setCellStyle(dateCellStyle);
	        }

	        // 사원번호
	        bodyRow.createCell(1).setCellValue(member.getMemberSeq());

	        // 부서코드
	        bodyRow.createCell(2).setCellValue(member.getFkDepartmentSeq());

	        // 직급코드
	        bodyRow.createCell(3).setCellValue(member.getFkGradeSeq());

	        // 이름
	        bodyRow.createCell(4).setCellValue(member.getMemberName());

	        // 이메일
	        bodyRow.createCell(5).setCellValue(member.getMemberEmail());
	    }

	    model.addAttribute("locale", Locale.KOREA);
	    model.addAttribute("workbookName", "CodeON사원정보");
	    model.addAttribute("workbook", workbook);
	    
	}

	// Excel 파일에 insert
	@Override
	public int add_memberList(List<Map<String, String>> paraMapList) {
		
		System.out.println("~~~~~~ paraMapList : " + paraMapList);

		int n = 0;
		
		try {
			
			for (Map<String, String> paraMap : paraMapList) {
				
				Member member = new Member();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

				member.setMemberName(paraMap.get("memberName"));
	            member.setFkDepartmentSeq(Integer.parseInt(paraMap.get("fkDepartmentSeq")));
	            member.setFkGradeSeq(Integer.parseInt(paraMap.get("fkGradeSeq")));
	            member.setMemberMobile(paraMap.get("memberMobile"));
	            member.setMemberBirthday(paraMap.get("memberBirthday"));
	            member.setMemberGender(Integer.parseInt(paraMap.get("memberGender")));
	            member.setMemberSalary(Long.parseLong(paraMap.get("memberSalary")) != 0 ?Long.parseLong(paraMap.get("memberSalary")) : 0);
	            member.setMemberHiredate(LocalDate.parse(paraMap.get("memberHiredate"), formatter));
	            member.setStampImage(paraMap.get("stampImage") != null ? paraMap.get("stampImage") : "");
	            
	            // 회원번호, 아이디, 비밀번호, 이메일 생성
	            String yearStr = String.valueOf(member.getMemberHiredate().getYear());
	            String deptStr = String.format("%02d", member.getFkDepartmentSeq());
	
	            Integer seq = ((Number) em.createNativeQuery("SELECT MEMBER_SEQ_GENERATOR.NEXTVAL FROM DUAL")
	                    .getSingleResult()).intValue();
	            String seqStr = String.format("%03d", seq);
	
	            String memberSeqStr = yearStr + deptStr + seqStr;
	            int memberSeqInt = Integer.parseInt(memberSeqStr);
	
	            member.setMemberSeq(memberSeqInt);
	            member.setMemberUserid(memberSeqStr);
	            member.setMemberPwd(memberSeqStr);
	            member.setMemberEmail(paraMap.get("memberEmail") + memberSeqStr + "@CodeON.com");
	            
	            System.out.println("~~~~~~~~~~~~~~~~~~~~~~ 확인용" + member);
	
	            // DB 저장
	            memberRepository.save(member);
	            n++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return n;
	}

	// tbl_member 테이블에서 부서명별 인원수 및 퍼센티지 가져오기 
	@Override
    public List<Map<String, Object>> memberCntByDeptname() {
        return mbrdao.memberCntByDeptname();
    }

    @Override
    public List<Map<String, Object>> memberCntByGender() {
        return mbrdao.memberCntByGender();
    }

	// 전체 회원 조회
	@Override
	public List<MemberDTO> findAll() {
		
        List<Member> members = jPAQueryFactory
                .selectFrom(member)
                .fetch();
        
        List<MemberDTO> memberDtoList = members.stream()
				   .map(Member::toDTO)
				   .collect(Collectors.toList());
        
        return memberDtoList;
	}
	
	@Override
    public List<Map<String, Object>> memberCntByHireYear() {
        return mbrdao.memberCntByHireYear();
    }

    @Override
    public List<Map<String, Object>> memberCntByHireYearGender() {
        return mbrdao.memberCntByHireYearGender();
    }

	@Override
	public List<MemberDTO> findByDept(int fkDepartmentSeq) {
		
		return mbrdao.findByDept(fkDepartmentSeq);
	}
}

