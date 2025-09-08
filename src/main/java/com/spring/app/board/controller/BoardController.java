package com.spring.app.board.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.app.board.domain.BoardDTO;
import com.spring.app.board.service.BoardService;
import com.spring.app.board.service.CommentService;
import com.spring.app.common.FileManager;
import com.spring.app.common.MyUtil;
import com.spring.app.domain.MemberDTO;
import com.spring.app.mail.domain.MailDTO;
import com.spring.app.mail.domain.MailUserStatusDTO;
import com.spring.app.mail.service.MailService;
import com.spring.app.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board/")
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;
    private final FileManager fileManager;
    private final MemberService memberService;
    private final MailService mailService;
    
    
    // 글쓰기 폼 요청(GET) 
    @GetMapping("add")
    public ModelAndView addForm(@RequestParam(value = "fkBoardTypeSeq", required = false) Integer fkBoardTypeSeq,
                                HttpSession session,                    
                                ModelAndView mav) {
        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        if (loginuser == null) {
            mav.addObject("message", "로그인 후 이용 가능합니다.");
            mav.addObject("loc", "/login/loginStart");
            mav.setViewName("msg");
            return mav;
        }
        if (fkBoardTypeSeq == null) fkBoardTypeSeq = 0;

        List<Map<String, Object>> boardTypeList = boardService.getBoardTypeList();
        List<Map<String, Object>> boardCategoryList = boardService.getBoardCategoryList();

        mav.addObject("boardTypeList", boardTypeList);
        mav.addObject("boardCategoryList", boardCategoryList);
        mav.addObject("fk_board_type_seq", fkBoardTypeSeq);
        mav.addObject("loginuser", loginuser);
        mav.setViewName("board/add");
        return mav;	
    }


 // 글쓰기 처리(POST)
    @PostMapping("add")
    public ModelAndView addPost(@RequestParam("fkBoardTypeSeq") Integer fkBoardTypeSeq,
                                @RequestParam("fkBoardCategorySeq") Integer fkBoardCategorySeq,
                                BoardDTO boardDto,
                                HttpSession session) {
        ModelAndView mav = new ModelAndView();

        try {
            MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
            boardDto.setFkMemberSeq(loginuser.getMemberSeq());
            boardDto.setMemberName(loginuser.getMemberName());
            boardDto.setFkBoardTypeSeq(fkBoardTypeSeq);
            boardDto.setFkBoardCategorySeq(fkBoardCategorySeq);

            // ===== 파일 업로드 처리 =====
            if (boardDto.getAttach() != null && !boardDto.getAttach().isEmpty()) {
                String originalFilename = boardDto.getAttach().getOriginalFilename();
                boardDto.setBoardFileOriName(originalFilename);

                String savedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
                boardDto.setBoardFileSaveName(savedFilename);
                boardDto.setBoardFileSize(boardDto.getAttach().getSize());

                String uploadDir = session.getServletContext().getRealPath("/resources/upload");
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                File savedFile = new File(dir, savedFilename);
                boardDto.getAttach().transferTo(savedFile);
            }

            // 1) 게시글 저장
            boardService.add(boardDto);

            // 2) 공지사항이면 메일 발송
            if (fkBoardCategorySeq == 0) { // 공지사항
                List<MemberDTO> targetMembers;

                if (fkBoardTypeSeq == 0) {
                    // 사내게시판인경우 전 직원
                    targetMembers = memberService.findAll();
                } else {
                    // 부서게시판인경우엔 해당 부서 직원만
                    targetMembers = memberService.findByDept(loginuser.getFkDepartmentSeq());
                }

                // 3) 수신자 리스트 구성 (본인 제외, null 체크)
                List<MailUserStatusDTO> statusList = targetMembers.stream()
                        .filter(m -> m.getMemberEmail() != null && !m.getMemberEmail().equals(loginuser.getMemberEmail()))
                        .map(m -> MailUserStatusDTO.builder()
                                .memberEmail(m.getMemberEmail())
                                .readStatus("0")
                                .importantStatus("0")
                                .build())
                        .toList();

                // 4) 모든 수신자를 콤마로 연결
                String allReceivers = statusList.stream()
                        .map(MailUserStatusDTO::getMemberEmail)
                        .reduce((a, b) -> a + "," + b)
                        .orElse("");

                // 5) MailDTO 구성
                for(MemberDTO m : targetMembers) {
                    if(m.getMemberEmail().length() > 50) continue;
                    MailDTO mail = MailDTO.builder()
                            .sendMemberEmail(loginuser.getMemberEmail())
                            .receiveMemberEmail(m.getMemberEmail())
                            .emailTitle("[공지] " + boardDto.getBoardTitle())
                            .emailContent(boardDto.getBoardContent())
                            .userStatusList(List.of(MailUserStatusDTO.builder()
                                                    .memberEmail(m.getMemberEmail())
                                                    .readStatus("0")
                                                    .importantStatus("0")
                                                    .build()))
                            .build();
                    mailService.write(mail);
                }
            }
            mav.setViewName("redirect:/board/list?fkBoardTypeSeq=" + boardDto.getFkBoardTypeSeq());

        } catch (IOException e) {
            e.printStackTrace();
            mav.addObject("errorMessage", "파일 업로드 중 오류가 발생했습니다.");
            mav.addObject("boardDto", boardDto);
            mav.setViewName("board/add");
        } catch (Exception e) {
            e.printStackTrace();
            mav.addObject("errorMessage", "글 작성 중 오류가 발생했습니다.");
            mav.addObject("boardDto", boardDto);
            mav.setViewName("board/add");
        }

        return mav;
    }


    // 게시물 목록
    @GetMapping("list")
    public ModelAndView list(ModelAndView mav, HttpServletRequest request, 
                             @RequestParam(name="searchType", defaultValue="") String searchType,
                             @RequestParam(name="searchword", defaultValue="") String searchword, 
                             @RequestParam(name="currentShowPageNo", defaultValue="1") String currentShowPageNo, 
                             @RequestParam(name="fkBoardCategorySeq", defaultValue="") String fkBoardCategorySeq,
                             @RequestParam(name="fkBoardTypeSeq", defaultValue="0") String fkBoardTypeSeq,
                             HttpServletResponse response) {

        HttpSession session = request.getSession();
        session.setAttribute("readCountPermission", "yes");

        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginuser");
        Integer userDept = (loginUser != null) ? loginUser.getFkDepartmentSeq() : null;

        // 부서명 조회
        String loginUserDeptName = "부서없음";
        if (userDept != null) {
            loginUserDeptName = boardService.getDepartmentNameBySeq(userDept);
            if (loginUserDeptName == null || loginUserDeptName.isEmpty()) {
                loginUserDeptName = "부서없음";
            }
        }
        mav.addObject("loginUserDeptName", loginUserDeptName);

        // 이전 페이지 referer 체크
        String referer = request.getHeader("Referer");
        if (referer == null) {
            mav.setViewName("redirect:/index");
            return mav;
        }

        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("searchType", searchType);
        paraMap.put("searchWord", searchword);
        paraMap.put("fkBoardCategorySeq", fkBoardCategorySeq);
        paraMap.put("fkBoardTypeSeq", Integer.parseInt(fkBoardTypeSeq));
        
        // 부서게시판 필터링
        if ("1".equals(fkBoardTypeSeq) && userDept != null) {
            paraMap.put("fkDepartmentSeq", userDept); // Integer 그대로
        }

        // -----------------------
        // 페이징 계산
        // -----------------------
        int totalCount = boardService.getTotalCount(paraMap);
     
        

        int sizePerPage = 10;
        int totalPage = (int) Math.ceil((double) totalCount / sizePerPage);
        System.out.println(">>> totalPage = " + totalPage);

        int pageNo = 1;
        try {
            pageNo = Integer.parseInt(currentShowPageNo);
            if (pageNo < 1 || pageNo > totalPage) pageNo = 1;
        } catch (NumberFormatException e) {
            pageNo = 1;
        }

        int startRno = ((pageNo - 1) * sizePerPage) + 1;
        int endRno = startRno + sizePerPage - 1;

        paraMap.put("sizePerPage", sizePerPage);
        paraMap.put("startRno", startRno);
        paraMap.put("endRno", endRno);

        List<BoardDTO> boardList = boardService.boardListSearch_withPaging(paraMap);
        
        // 이번 주 인기글
        Integer deptFilter = "1".equals(fkBoardTypeSeq) ? userDept : null;
        List<BoardDTO> weeklyPopular = boardService.getWeeklyPopularBoard(Integer.parseInt(fkBoardTypeSeq), deptFilter);
        mav.addObject("weeklyPopular", weeklyPopular);
        
        for (BoardDTO board : boardList) {
            int recommendCount = commentService.getRecommendCount(board.getBoardSeq());
            board.setRecommendCount(recommendCount);
        } //추천수 recommendCount 가져와서 boardList에 추가 
        
        mav.addObject("boardList", boardList);

        if (!"".equals(searchType)) {
            mav.addObject("paraMap", paraMap);
        }

        // -----------------------
        // 페이징바 생성
        // -----------------------
        int blockSize = 10;
        int loop = 1;
        int pageStart = ((pageNo - 1) / blockSize) * blockSize + 1;
        String url = "list";
        String fkBoardTypeParam = "&fkBoardTypeSeq=" + fkBoardTypeSeq;

        String pageBar = "<ul style='list-style:none;'>";

        if (totalPage > 0) {
            pageBar += "<li style='display:inline-block; width:70px; font-size:12pt;'><a href='" + url + "?searchType=" + searchType + "&searchword=" + searchword + fkBoardTypeParam + "&currentShowPageNo=1'>[맨처음]</a></li>";

            if (pageStart != 1) {
                pageBar += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='" + url + "?searchType=" + searchType + "&searchword=" + searchword + fkBoardTypeParam + "&currentShowPageNo=" + (pageStart - 1) + "'>[이전]</a></li>";
            }

            while (!(loop > blockSize || pageStart > totalPage)) {
                if (pageStart == pageNo) {
                    pageBar += "<li style='display:inline-block; width:30px; font-size:12pt; border:solid 1px gray; color:red; padding:2px 4px;'>" + pageStart + "</li>";
                } else {
                    pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='" + url + "?searchType=" + searchType + "&searchword=" + searchword + fkBoardTypeParam + "&currentShowPageNo=" + pageStart + "'>" + pageStart + "</a></li>";
                }
                loop++;
                pageStart++;
            }

            if (pageStart <= totalPage) {
                pageBar += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='" + url + "?searchType=" + searchType + "&searchword=" + searchword + fkBoardTypeParam + "&currentShowPageNo=" + pageStart + "'>[다음]</a></li>";
            }

            pageBar += "<li style='display:inline-block; width:70px; font-size:12pt;'><a href='" + url + "?searchType=" + searchType + "&searchword=" + searchword + fkBoardTypeParam + "&currentShowPageNo=" + totalPage + "'>[마지막]</a></li>";
        }

        pageBar += "</ul>";
        mav.addObject("pageBar", pageBar);

        mav.addObject("totalCount", totalCount);
        mav.addObject("currentShowPageNo", pageNo);
        mav.addObject("sizePerPage", sizePerPage);

        
        // 현재 URL 저장 (쿠키)
        String listURL = MyUtil.getCurrentURL(request);
        Cookie cookie = new Cookie("listURL", listURL);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setPath("/CodeOn/board/");
        response.addCookie(cookie);

        mav.setViewName("board/list");
        return mav;
    }

    @GetMapping("view")
    public String view(@RequestParam("boardSeq") String boardSeq,
                       Model model,
                       HttpServletRequest request,
                       RedirectAttributes redirectAttrs) {

        // 게시글 상세 조회
        BoardDTO board = boardService.getBoardDetail(boardSeq);

        if (board == null) {
            redirectAttrs.addFlashAttribute("errorMsg", "존재하지 않는 게시글입니다.");
            return "redirect:/board/list";
        }

        // 추천 수 조회
        int recommendCount = commentService.getRecommendCount(Integer.valueOf(boardSeq));
        board.setRecommendCount(recommendCount);

        
        HttpSession session = request.getSession();
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginuser");
        Integer userDept = (loginUser != null) ? loginUser.getFkDepartmentSeq() : null;

        // 이전/다음 글 조회를 위한 paraMap
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("boardSeq", boardSeq);
        paraMap.put("fkBoardTypeSeq", board.getFkBoardTypeSeq());

        // 부서게시판이라면 부서번호도 같이 조건으로 줌
        if ("1".equals(board.getFkBoardTypeSeq()) && userDept != null) {
            paraMap.put("fkDepartmentSeq", userDept);
        }

        BoardDTO prevBoard = boardService.getPrevBoard(paraMap);
        BoardDTO nextBoard = boardService.getNextBoard(paraMap);

        // JSP로 데이터 전달
        model.addAttribute("board", board);
        model.addAttribute("prevBoard", prevBoard);
        model.addAttribute("nextBoard", nextBoard);

        return "board/view";
    }

   
    
    
    
 // 1. 글 수정 페이지 이동(GET)
    @GetMapping("edit")
    public ModelAndView editForm(@RequestParam("boardSeq") String boardSeq,
                                 HttpSession session) {
        ModelAndView mav = new ModelAndView();

        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        if (loginuser == null) {
            mav.addObject("message", "로그인 후 이용 가능합니다.");
            mav.addObject("loc", "/login/loginStart");
            mav.setViewName("msg");
            return mav;
        }

        BoardDTO board = boardService.getBoardDetail(boardSeq);
        if (board == null) {
            mav.addObject("message", "존재하지 않는 게시글입니다.");
            mav.addObject("loc", "/board/list");
            mav.setViewName("msg");
            return mav;
        }

        // 작성자 체크
        if (loginuser.getMemberSeq() != board.getFkMemberSeq()) {
            mav.addObject("message", "본인 글만 수정 가능합니다.");
            mav.addObject("loc", "/board/view?boardSeq=" + boardSeq);
            mav.setViewName("msg");
            return mav;
        }

        // 게시판 타입/카테고리
        List<Map<String, Object>> boardTypeList = boardService.getBoardTypeList();
        List<Map<String, Object>> boardCategoryList = boardService.getBoardCategoryList();

        mav.addObject("board", board);
        mav.addObject("boardTypeList", boardTypeList);
        mav.addObject("boardCategoryList", boardCategoryList);
        mav.addObject("loginuser", loginuser);
        mav.setViewName("board/edit"); // edit.jsp 호출
        return mav;
    }

    // 2. 글 수정 처리(POST)
    @PostMapping("edit")
    public ModelAndView editPost(BoardDTO boardDto, HttpSession session) {
        ModelAndView mav = new ModelAndView();

        try {
            MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
            if (loginuser == null || loginuser.getMemberSeq() != boardDto.getFkMemberSeq()) {
                mav.addObject("message", "본인 글만 수정 가능합니다.");
                mav.addObject("loc", "/board/list");
                mav.setViewName("msg");
                return mav;
            }

            // ===== 파일 업로드 처리 =====
            if (boardDto.getAttach() != null && !boardDto.getAttach().isEmpty()) {
                String originalFilename = boardDto.getAttach().getOriginalFilename();
                boardDto.setBoardFileOriName(originalFilename);

                String savedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
                boardDto.setBoardFileSaveName(savedFilename);

                boardDto.setBoardFileSize(boardDto.getAttach().getSize());

                String uploadDir = session.getServletContext().getRealPath("/resources/upload");
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                File savedFile = new File(dir, savedFilename);
                boardDto.getAttach().transferTo(savedFile);

                // 기존 첨부파일 삭제
                BoardDTO oldBoard = boardService.getBoardDetail(String.valueOf(boardDto.getBoardSeq()));
                if (oldBoard.getBoardFileSaveName() != null && !oldBoard.getBoardFileSaveName().isEmpty()) {
                    File oldFile = new File(uploadDir, oldBoard.getBoardFileSaveName());
                    if (oldFile.exists()) oldFile.delete();
                }
            }

            boardService.updateBoard(boardDto);

            mav.setViewName("redirect:/board/view?boardSeq=" + boardDto.getBoardSeq());

        } catch (IOException e) {
            e.printStackTrace();
            mav.addObject("errorMessage", "파일 업로드 중 오류가 발생했습니다.");
            mav.addObject("boardDto", boardDto);
            mav.setViewName("board/edit");
        } catch (Exception e) {
            e.printStackTrace();
            mav.addObject("errorMessage", "글 수정 중 오류가 발생했습니다.");
            mav.addObject("boardDto", boardDto);
            mav.setViewName("board/edit");
        }

        return mav;
    }  
    
    
    
    
    
    
 // 게시글 삭제 
    @PostMapping("delete")
    @ResponseBody
    public Map<String, Object> delete(@RequestParam("boardSeq") String boardSeq,
                                      HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        if (loginuser == null) {
            result.put("status", "fail");
            result.put("message", "로그인 후 사용 가능합니다.");
            return result;
        }
        
        BoardDTO board = boardService.getBoardDetail(boardSeq);
        if (board == null) {
            result.put("status", "fail");
            result.put("message", "존재하지 않는 게시글입니다.");
            return result;
        }
        
        // 작성자만 삭제 가능
        if (loginuser.getMemberSeq() != board.getFkMemberSeq()) {
            result.put("status", "fail");
            result.put("message", "본인 글만 삭제 가능합니다.");
            return result;
        }
        
        try {
            // 첨부파일 삭제
            if (board.getBoardFileSaveName() != null && !board.getBoardFileSaveName().isEmpty()) {
                String uploadDir = session.getServletContext().getRealPath("/resources/upload");
                File file = new File(uploadDir, board.getBoardFileSaveName());
                if (file.exists()) file.delete();
            }
            
            // DB 삭제 (댓글/대댓글테이블 시퀀스에 제약조건 DELETE CASCADE 처리해놓음)
            boardService.delete(boardSeq);
            
            result.put("status", "success");
            result.put("message", "게시글이 삭제되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "fail");
            result.put("message", "삭제 중 오류가 발생했습니다.");
        }
        
        return result;
    }

}