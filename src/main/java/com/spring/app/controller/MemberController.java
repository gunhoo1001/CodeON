package com.spring.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.app.domain.MemberDTO;
import com.spring.app.entity.Member;
import com.spring.app.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/member/")
@RequiredArgsConstructor  // final 필드 생성자 주입
public class MemberController {
	
	private final MemberService memberService;
	
	@GetMapping("register")
	public String memberRegister() {
		return "member/register";
	}

	@GetMapping("list")
	public String memberList(@RequestParam(name="searchType", defaultValue="")  String searchType,
 		   					 @RequestParam(name="searchWord", defaultValue="")  String searchWord,
 		   					 @RequestParam(name="gender", defaultValue="")  String gender,
 		   					 @RequestParam(value="pageno",    defaultValue="1") int currentShowPageNo,
 		   					 Model model,
 		            		 HttpServletRequest request,
 		            		 HttpServletResponse response) {
		
		int sizePerPage = 4;
		int totalPage = 0;
		long totalDataCount = 0;
		String pageBar = "";
		
		try {
			Page<Member> pageMember = memberService.getPageMember(searchType, searchWord, gender, currentShowPageNo, sizePerPage);
			totalPage = pageMember.getTotalPages(); // 전체 페이지 개수
	//		System.out.println("~~~ 확인용 전체 페이지 개수 : " + totalPage);
			
			if (currentShowPageNo > totalPage) {
				currentShowPageNo = totalPage;
				pageMember = memberService.getPageMember(searchType, searchWord, gender, currentShowPageNo, sizePerPage);
			}
			
			// 전체 데이터의 개수
			totalDataCount = pageMember.getTotalElements();
	//		System.out.println("~~~ 확인용 전체 데이터 개수 : " + totalDataCount);
			
			// 현재 페이지의 데이터 목록
			List<Member> memberList = pageMember.getContent();
			
			// 현재 페이지의 데이터 목록인 List<Board> 를 List<BoardDTO> 로 변환한다.
			List<MemberDTO> MemberDtoList = memberList.stream()
											        .map(Member::toDTO)
											        .collect(Collectors.toList());

			model.addAttribute("MemberDtoList", MemberDtoList);
			
			if (!"".equals(searchType) && !"".equals(searchWord)) {
				model.addAttribute("searchType", searchType); // view 단 페이지에서 검색타입 유지
				model.addAttribute("searchWord", searchWord); // view 단 페이지에서 검색어 유지
			}
			
			if (!"".equals(gender)) {
			    model.addAttribute("gender", gender);
			}
			
			// === 페이지바 만들기 시작 === //
			int blockSize = 10;

	        int loop = 1;

	        int pageno = ((currentShowPageNo - 1)/blockSize) * blockSize + 1;

	        pageBar = "<ul style='list-style:none;'>";
	        String url = "/member/list";
	        
	        // === [맨처음][이전] 만들기 === //
	        if(pageno != 1) {
	           pageBar += "<li style='display:inline-block; width:70px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&pageno=1'>[맨처음]</a></li>";
	           pageBar += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&pageno="+(pageno-1)+"'>[이전]</a></li>"; 
	        }
	        
	        while( !(loop > blockSize || pageno > totalPage) ) {
	           
	           if(pageno == currentShowPageNo) {
	              pageBar += "<li style='display:inline-block; width:30px; font-size:12pt; border:solid 1px gray; color:red; padding:2px 4px;'>"+pageno+"</li>";
	           }
	           else {
	              pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&pageno="+pageno+"'>"+pageno+"</a></li>"; 
	           }
	           
	           loop++;
	           pageno++;
	        }// end of while------------------------
	        
	        // === [다음][마지막] 만들기 === //
	        if(pageno <= totalPage) {
	           pageBar += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&pageno="+pageno+"'>[다음]</a></li>";
	           pageBar += "<li style='display:inline-block; width:70px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&pageno="+totalPage+"'>[마지막]</a></li>"; 
	        }
	        
	        pageBar += "</ul>";
	        
	        model.addAttribute("pageBar", pageBar);
			
			// === 페이지바 만들기 끝 === //
			
			model.addAttribute("totalDataCount", totalDataCount); // 페이징 처리시 보여주는 순번을 나타내기 위한 것임. 
	        model.addAttribute("currentShowPageNo", currentShowPageNo); // 페이징 처리시 보여주는 순번을 나타내기 위한 것임.
	        model.addAttribute("sizePerPage", sizePerPage); // 페이징 처리시 보여주는 순번을 나타내기 위한 것임.
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "member/list";
	}
	
    @GetMapping("sign/members")
    public List<Member> getMembers() {
        return memberService.getAllMembersOrderByDept();
    }
    
	// 직원 수정
	@GetMapping("update")
	public String update(@RequestParam("memberSeq") String memberSeq, Model model) {

	    MemberDTO mbrDto = memberService.getMemberOne(memberSeq);
	    System.out.println(mbrDto);
	    model.addAttribute("mbrDto", mbrDto);

	    return "member/update";
	}
	
	// Excel 파일 다운
	@PostMapping("downloadExcelFile")
	public String downloadExcelFile(
	        @RequestParam(name="searchType", defaultValue="") String searchType,
	        @RequestParam(name="searchWord", defaultValue="") String searchWord,
	        @RequestParam(name="gender", defaultValue="") String gender,
	        Model model) {

	    Map<String, String> paraMap = new HashMap<>();

	    if (!searchType.isEmpty()) paraMap.put("searchType", searchType);
	    if (!searchWord.isEmpty()) paraMap.put("searchWord", searchWord);
	    if (!gender.isEmpty()) paraMap.put("gender", gender);

	    // 서비스에서 paraMap 기반으로 회원 목록 조회 후 Excel로 변환
	    memberService.memberList_to_Excel(paraMap, model);

	    return "excelDownloadView"; // ExcelView 구현체
	}
	@GetMapping("chart")
	public String chart() {
		return "member/chart";
	}

   @GetMapping("chat")
   String chat() {
	   return "member/chat";
   }
	
}

