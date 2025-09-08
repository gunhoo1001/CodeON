package com.spring.app.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.spring.app.domain.MemberDTO;
import com.spring.app.entity.Member;
import com.spring.app.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/memberInfo/")
@RequiredArgsConstructor
public class MemberRestController {
	
	private final MemberService memberService;

	// 직원등록
	@PostMapping("register")
	public Map<String, Member> register(MemberDTO mbrDto) {
		
		Member member = Member.builder()
							  .memberName(mbrDto.getMemberName())
							  .memberUserid(mbrDto.getMemberUserid())
							  .memberPwd(mbrDto.getMemberPwd())
							  .memberEmail(mbrDto.getMemberEmail())
							  .memberMobile(mbrDto.getMemberMobile())
							  .fkGradeSeq(mbrDto.getFkGradeSeq())
							  .fkDepartmentSeq(mbrDto.getFkDepartmentSeq())
							  .memberBirthday(mbrDto.getMemberBirthday())
							  .memberHiredate(mbrDto.getMemberHiredate())
							  .memberGender(mbrDto.getMemberGender())
							  .build();
				
		Member mbr = memberService.registerMember(member);
		
		Map<String, Member> map = new HashMap<>();
		map.put("member", mbr);
		
		return map;
	}
	
	// 직원수정
	@PostMapping("update")
	public Map<String, Member> update(MemberDTO mbrDto) {
	    
	    Member member = Member.builder()
	                          .memberSeq(mbrDto.getMemberSeq())
	                          .fkGradeSeq(mbrDto.getFkGradeSeq())
	                          .fkDepartmentSeq(mbrDto.getFkDepartmentSeq())
	                          .memberName(mbrDto.getMemberName())
	                          .memberUserid(mbrDto.getMemberUserid())
	                          .memberPwd(mbrDto.getMemberPwd())
	                          .memberEmail(mbrDto.getMemberEmail())   // ✅ 누락 방지
	                          .memberSalary(mbrDto.getMemberSalary()) // ✅ 누락 방지
	                          .memberHiredate(mbrDto.getMemberHiredate())
	                          .memberMobile(mbrDto.getMemberMobile())
	                          .memberBirthday(mbrDto.getMemberBirthday())
	                          .memberGender(mbrDto.getMemberGender())
	                          .stampImage(mbrDto.getStampImage())     // ✅ 누락 방지
	                          .build();
	    
	    Member mbr = memberService.updateMember(member);
	    
	    Map<String, Member> map = new HashMap<>();
	    map.put("member", mbr);
	    
	    return map;
	}

	
	// 회원 삭제
	@DeleteMapping("delete")
	public Map<String, Integer> delete(@RequestParam(name="memberSeq") String memberSeq) {
		
		int n_memberSeq = Integer.parseInt(memberSeq);
		
		int n = memberService.delete(n_memberSeq);
		
		Map<String, Integer> map = new HashMap<>();
		map.put("n", n);
		
		return map;
	}
	
	// Excel 파일 업로드
	@PostMapping("uploadExcelFile")
	public Map<String, Integer> uploadExcelFile(MultipartHttpServletRequest mtp_request) { // MultipartHttpServletRequest 기존 HttpServletRequest 역할과 file 처리까지 포함한다
		
		Map<String, Integer> resultMap = new HashMap<>();
		
		MultipartFile mtp_excel_file = mtp_request.getFile("excel_file");
		
		if (mtp_excel_file != null) {
			
			try {
				// == MultipartFile 을 File 로 변환하기 시작 == //
	            // WAS 의 webapp 의 절대경로를 알아와야 한다.
	            HttpSession session = mtp_request.getSession();
	            String root = session.getServletContext().getRealPath("/");
	            String path = root + "resources"+File.separator+"files";
	            
	            File excel_file = new File(path+File.separator+mtp_excel_file.getOriginalFilename());
	            mtp_excel_file.transferTo(excel_file);
	            // == MultipartFile 을 File 로 변환하기 끝 == //
	            
	            OPCPackage opcPackage = OPCPackage.open(excel_file);
	            /* 아파치 POI(Apache POI)는 아파치 소프트웨어 재단에서 만든 라이브러리로서 마이크로소프트 오피스파일 포맷을 순수 자바 언어로서 읽고 쓰는 기능을 제공한다. 
	               주로 워드, 엑셀, 파워포인트와 파일을 지원하며 최근의 오피스 포맷인 Office Open XML File Formats(OOXML, 즉 xml 기반의 *.docx, *.xlsx, *.pptx 등) 이나 아웃룩, 비지오, 퍼블리셔 등으로 지원 파일 포맷을 늘려가고 있다. 
	            */
	            
	            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
	            
	            // 첫번째 시트 불러오기
	            XSSFSheet sheet = workbook.getSheetAt(0);
	            
	            List<Map<String, String>> paraMapList = new ArrayList<>();
	            
	            for (int i=1;i<=sheet.getLastRowNum();i++) {
	            	Map<String, String> paraMap = new HashMap<>();
	            	
	            	XSSFRow row = sheet.getRow(i);
	            	
	            	// 행이 존재하지 않으면 건너뛴다.
	            	if (row == null) {
	            		continue;	
	            	}
	            	
	            	// 행의 1번째 열(입사일자)
	            	XSSFCell cell = row.getCell(0);
	            	
	            	if (cell != null) {
	            		paraMap.put("memberHiredate", cellReader(cell));
	            	}
	            	// 행의 2번째 열(부서)
	            	cell = row.getCell(1);
	            	
	            	if (cell != null) {
	            		paraMap.put("fkDepartmentSeq", cellReader(cell));
	            	}
	            	
	            	// 행의 3번째 열(직급)
	            	cell = row.getCell(2);
	            	
	            	if (cell != null) {
	            		paraMap.put("fkGradeSeq", cellReader(cell));
	            	}
	            	
	            	// 행의 4번째 열(이름)
	            	cell = row.getCell(3);
	            	
	            	if (cell != null) {
	            		paraMap.put("memberName", cellReader(cell));
	            	}
	            	
	            	// 행의 5번째 열(이메일)
	            	cell = row.getCell(4);
	            	
	            	if (cell != null) {
	            		paraMap.put("memberEmail", cellReader(cell));
	            	}
	            	
	            	// 행의 6번째 열(생년월일)
	            	cell = row.getCell(5);
	            	
	            	if (cell != null) {
	            		paraMap.put("memberBirthday", cellReader(cell));
	            	}
	            	
	            	// 행의 7번째 열(전화번호)
	            	cell = row.getCell(6);
	            	
	            	if (cell != null) {
	            		paraMap.put("memberMobile", cellReader(cell));
	            	}
	            	
	            	// 행의 8번째 열(성별)
	            	cell = row.getCell(7);
	            	
	            	if (cell != null) {
	            		paraMap.put("memberGender", cellReader(cell));
	            	}
	            	
	            	// 행의 9번째 열(월급)
	            	cell = row.getCell(8);
	            	
	            	if (cell != null) {
	            		paraMap.put("memberSalary", cellReader(cell));
	            	}
	            	
	            	// 행의 10번째 열(도장이미지)
	            	cell = row.getCell(9);
	            	
	            	if (cell != null) {
	            		paraMap.put("stampImage", cellReader(cell));
	            	}
	            	
	            	paraMapList.add(paraMap);
	            } // end of for----------------
	            
	            workbook.close();
	            
	            int insert_count = memberService.add_memberList(paraMapList);
	            
	            if (insert_count == paraMapList.size()) {
	            	resultMap.put("result", 1);
	            } else {
	            	resultMap.put("result", 0);
	            }
	            
	            excel_file.delete(); // 업로드된 파일 삭제하기
	            
			} catch (Exception e) {
				e.printStackTrace();
				resultMap.put("result", 0);
			}
		} else {
			resultMap.put("result", 0);
		}
		
		return resultMap;
	}
	
	@SuppressWarnings("incomplete-switch")	
	private static String cellReader(XSSFCell cell) {
		
		String value = "";
		CellType ct = cell.getCellType();
		if(ct != null) {
			switch(cell.getCellType()) {
            	case FORMULA:
            		value = cell.getCellFormula()+"";
            		break;
            	case NUMERIC:
            	    if (DateUtil.isCellDateFormatted(cell)) {
            	        Date date = cell.getDateCellValue();
            	        value = new SimpleDateFormat("yyyyMMdd").format(date);
            	    } else {
            	        value = String.valueOf((long) cell.getNumericCellValue()); // 정수라면 소수점 제거
            	    }
            	    break;
            	case STRING:
            		value = cell.getStringCellValue()+"";
            		break;
            	case BOOLEAN:
            		value = cell.getBooleanCellValue()+"";
            		break;
            	case ERROR:
            		value = cell.getErrorCellValue()+"";
            		break;
			}
		}
		return value; 
	}
	
	@GetMapping("detail")
	public MemberDTO detail(@RequestParam(name="memberSeq") String memberSeq) {
		return memberService.getMemberOne(memberSeq);
	}
	
	// 부서별 인원통계
	@GetMapping("memberCntByDeptname")
    public List<Map<String, Object>> memberCntByDeptname() {
        return memberService.memberCntByDeptname();
    }

    @GetMapping("memberCntByGender")
    public List<Map<String, Object>> memberCntByGender() {
        return memberService.memberCntByGender();
    }

    @GetMapping("memberCntByHireYear")
    public List<Map<String, Object>> memberCntByHireYear() {
        return memberService.memberCntByHireYear();
    }

    @GetMapping("memberCntByHireYearGender")
    public List<Map<String, Object>> memberCntByHireYearGender() {
        return memberService.memberCntByHireYearGender();
    }
	
}
