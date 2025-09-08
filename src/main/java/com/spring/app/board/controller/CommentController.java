package com.spring.app.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.app.board.domain.CommentDTO;
import com.spring.app.board.domain.ReplyDTO;
import com.spring.app.board.service.CommentService;
import com.spring.app.domain.MemberDTO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment/")
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("add")
    @ResponseBody
    public String addComment(@RequestParam("fkBoardSeq") Integer fkBoardSeq,
                             @RequestParam("commentContent") String commentContent,
                             HttpSession session) {

        if (fkBoardSeq == null) return "fail"; // fkBoardSeq 누락 방지

        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        if (loginuser == null) return "fail";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("fkBoardSeq", fkBoardSeq);
        paramMap.put("fkMemberSeq", loginuser.getMemberSeq());
        paramMap.put("commentContent", commentContent);

        int result = commentService.addComment(paramMap);
        return result > 0 ? "success" : "fail";
    }

    // 댓글 목록 조회
    @GetMapping("list")
    @ResponseBody
    public List<CommentDTO> getCommentList(@RequestParam("fkBoardSeq") Integer fkBoardSeq,
                                           HttpSession session) {
        if (fkBoardSeq == null) return List.of();

        List<CommentDTO> commentList = commentService.getCommentList(fkBoardSeq);

        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        Integer loginUserSeq = (loginuser != null ? loginuser.getMemberSeq() : null);
        
        for (CommentDTO comment : commentList) {
            if (loginUserSeq != null && loginUserSeq.equals(comment.getFkMemberSeq())) {
                comment.setMine(true);
            } else {
                comment.setMine(false);
            }
        }

        return commentList;
    }
    @PostMapping("addReply")
    @ResponseBody
    public String addReply(@RequestParam("parentCommentSeq") Integer parentCommentSeq,
                           @RequestParam("fkBoardSeq") Integer fkBoardSeq,
                           @RequestParam("commentContent") String content,
                           HttpSession session) {

        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        if (loginuser == null) return "fail";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("fkCommentSeq", parentCommentSeq);
        paramMap.put("fkMemberSeq", loginuser.getMemberSeq());
        paramMap.put("replyContent", content);

        int result = commentService.addReply(paramMap);
        return result > 0 ? "success" : "fail";
    }
    
    @GetMapping("listReply")
    @ResponseBody
    public List<ReplyDTO> getReplyList(@RequestParam("parentCommentSeq") Integer parentCommentSeq,
                                       HttpSession session) {
        if (parentCommentSeq == null) return List.of();

        List<ReplyDTO> replyList = commentService.getReplyList(parentCommentSeq);

        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        Integer loginUserSeq = (loginuser != null ? loginuser.getMemberSeq() : null);

        for (ReplyDTO reply : replyList) {
            reply.setMine(loginUserSeq != null && loginUserSeq.equals(reply.getFkMemberSeq()));
        }

        return replyList;
    }
    //댓글 수정
    @PostMapping("edit")
    @ResponseBody
    public Map<String, Object> editComment(@RequestBody CommentDTO cdto,
                                           HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        if (loginuser == null) {
            result.put("status", "fail");
            result.put("message", "로그인 후 사용 가능합니다.");
            return result;
        }

        // 세션 회원번호 넣기
        cdto.setFkMemberSeq(loginuser.getMemberSeq());

        int res = commentService.editComment(cdto);
        if (res > 0) {
            result.put("status", "success");
            result.put("message", "댓글이 수정되었습니다.");
        } else {
            result.put("status", "fail");
            result.put("message", "수정 실패 또는 본인 댓글이 아닙니다.");
        }
        return result;
    }
    
    
    // 댓글 삭제
    @PostMapping("delete")
    @ResponseBody
    public Map<String, Object> deleteComment(@RequestParam("commentSeq") Integer commentSeq,
                                             HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        if (loginuser == null) {
            result.put("status", "fail");
            result.put("message", "로그인 후 사용 가능합니다.");
            return result;
        }

        int res = commentService.deleteComment(commentSeq, loginuser.getMemberSeq());
        if (res > 0) {
            result.put("status", "success");
            result.put("message", "댓글이 삭제되었습니다.");
        } else {
            result.put("status", "fail");
            result.put("message", "삭제 실패 또는 본인 댓글이 아닙니다.");
        }
        return result;
    }
    
    
 // 대댓글 수정
    @PostMapping("editReply")
    @ResponseBody
    public Map<String, Object> editReply(@RequestBody ReplyDTO rdto,
                                         HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        if (loginuser == null) {
            result.put("status", "fail");
            result.put("message", "로그인 후 사용 가능합니다.");
            return result;
        }

        // 세션 회원번호 넣기
        rdto.setFkMemberSeq(loginuser.getMemberSeq());

        int res = commentService.editReply(rdto);
        if (res > 0) {
            result.put("status", "success");
            result.put("message", "대댓글이 수정되었습니다.");
        } else {
            result.put("status", "fail");
            result.put("message", "수정 실패 또는 본인 대댓글이 아닙니다.");
        }
        
        return result;
    }
    
    
    
    @PostMapping("deleteReply")
    @ResponseBody
    public Map<String, Object> deleteReply(@RequestParam("replySeq") Integer replySeq,
                                           HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        if (loginuser == null) {
            result.put("status", "fail");
            result.put("message", "로그인 후 사용 가능합니다.");
            return result;
        }

        int res = commentService.deleteReply(replySeq, loginuser.getMemberSeq());
        if (res > 0) {
            result.put("status", "success");
            result.put("message", "대댓글이 삭제되었습니다.");
        } else {
            result.put("status", "fail");
            result.put("message", "삭제 실패 또는 본인 대댓글이 아닙니다.");
        }
        return result;
    }
    
    
    
    //추천
    @PostMapping("toggleRecommend")
    @ResponseBody
    public Map<String, Object> toggleRecommend(@RequestParam("fkBoardSeq") Integer fkBoardSeq,
                                               HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");

        if(loginuser == null) {
            result.put("status", "fail");
            return result;
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("fkBoardSeq", fkBoardSeq);
        paramMap.put("fkMemberSeq", loginuser.getMemberSeq());

        try {
            boolean exists = commentService.existsRecommend(paramMap);
            if(exists) {
                commentService.removeRecommend(paramMap); // 추천 취소
                result.put("status", "removed");
            } else {
                commentService.addRecommend(paramMap); // 추천 추가
                result.put("status", "added");
            }
            int newCount = commentService.getRecommendCount(fkBoardSeq);
            result.put("newCount", newCount);
        } catch(Exception e) {
            e.printStackTrace();
            result.put("status", "fail");
        }

        return result;
    }
 // 현재 로그인 사용자의 추천 여부 체크
    @GetMapping("checkRecommend")
    @ResponseBody
    public Map<String, Object> checkRecommend(@RequestParam("fkBoardSeq") Integer fkBoardSeq,
                                              HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");

        if (loginuser == null) {
            result.put("exists", false);
            result.put("count", commentService.getRecommendCount(fkBoardSeq));
            return result;
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("fkBoardSeq", fkBoardSeq);
        paramMap.put("fkMemberSeq", loginuser.getMemberSeq());

        boolean exists = commentService.existsRecommend(paramMap);

        result.put("exists", exists);
        result.put("count", commentService.getRecommendCount(fkBoardSeq));

        return result;
    }
    
    
    // 게시글 추천수 조회
    @GetMapping("recommendCount")
    @ResponseBody
    public Map<String, Object> getRecommendCount(@RequestParam("fkBoardSeq") Integer fkBoardSeq) {
        Map<String, Object> result = new HashMap<>();
        if (fkBoardSeq == null) {
            result.put("status", "fail");
            result.put("count", 0);
            return result;
        }

        int count = commentService.getRecommendCount(fkBoardSeq);
        result.put("status", "success");
        result.put("count", count);

        return result;
    }
    
    //추천한 사원 조회
    @GetMapping("recommendMembers")
    @ResponseBody
    public List<String> getRecommendMembers(@RequestParam("fkBoardSeq") Integer fkBoardSeq) {
        return commentService.getRecommendMemberNames(fkBoardSeq);
    }
    
    
}
