package com.spring.app.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.spring.app.common.FileManager;
import com.spring.app.domain.MemberDTO;
import com.spring.app.domain.SignlineDTO;
import com.spring.app.entity.Draft;
import com.spring.app.entity.Signline;
import com.spring.app.entity.SignlineMember;
import com.spring.app.model.DraftRepository;
import com.spring.app.model.MemberRepository;
import com.spring.app.model.SignlineRepository;
import com.spring.app.service.MemberService;
import com.spring.app.service.SignService;
import com.spring.app.service.SignlineService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sign")
public class SignController {

    private final FileManager fileManager;
    private final MemberRepository memberRepository;
    private final SignlineRepository signlineRepository;
    private final DraftRepository draftRepository;

    private final SignService signService;
    private final SignlineService signlineService;
    private final MemberService memberService;

    /* ===================== 메인/요약 ===================== */
    @GetMapping("main")
    public String signmain(HttpSession session, Model model) {
        Long me = Long.valueOf(((MemberDTO)session.getAttribute("loginuser")).getMemberSeq());
        model.addAttribute("inboxPreview",   signService.buildInboxPreview(me, 3));
        model.addAttribute("sentPreview",    signService.buildSentPreview(me, 3));
        model.addAttribute("historyPreview", signService.buildHistoryPreview(me, 3));
        return "/sign/signmain";
    }

    /* ===================== 작성 화면 ===================== */
    @GetMapping("add")
    public String signadd(HttpSession session, Model model) {
        Long previewNo = 0L;
        try { previewNo = draftRepository.peekNextDraftNo(); } catch (Exception ignore) {}
        model.addAttribute("previewNo", previewNo);

        MemberDTO login = (MemberDTO) session.getAttribute("loginuser");
        String deptName = "";
        if (login != null) {
            if (login.getDepartment() != null && login.getDepartment().getDepartmentName() != null) {
                deptName = login.getDepartment().getDepartmentName();
            } else if (login.getFkDepartmentSeq() > 0) {
                try { deptName = memberRepository.findDeptName(login.getFkDepartmentSeq()); } catch (Exception ignore) {}
            }
        }
        model.addAttribute("loginDeptName", deptName);
        return "/sign/signadd";
    }

    /* ===================== 환경설정(도장) ===================== */
    @GetMapping("setting")
    public String signsetting(HttpSession session, HttpServletRequest request, Model model) {
        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        if (loginuser != null) {
            String savedFilename = memberRepository.findStampImageByUserid(loginuser.getMemberUserid());
            if (savedFilename != null && !savedFilename.isBlank()) {
                String ctxPath = request.getContextPath();
                String stampUrl = ctxPath + "/resources/stamp_upload/" + savedFilename;
                model.addAttribute("stampFilename", savedFilename);
                model.addAttribute("stampUrl", stampUrl);
            }
        }
        return "/sign/signsetting";
    }

    @PostMapping("stampImageSave")
    public void stampImageSave(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String root = session.getServletContext().getRealPath("/");
        String path = root + "resources" + File.separator + "stamp_upload";

        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();

        try {
            String filename = request.getHeader("file-name");
            InputStream is = request.getInputStream();
            MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");

            String newFilename = signService.saveStamp(loginuser.getMemberUserid(), filename, is, path);

            String ctxPath = request.getContextPath();
            String fileUrl = ctxPath + "/resources/stamp_upload/" + newFilename;

            response.setContentType("application/json;charset=UTF-8");
            try (var out = response.getWriter()) {
                out.print("{\"result\":\"success\", \"url\":\"" + fileUrl + "\"}");
            }
        } catch (Exception e) {
            try {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print("{\"result\":\"fail\"}");
            } catch (IOException ignore) {}
        }
    }

    @PostMapping("stampImageDelete")
    @ResponseBody
    @Transactional
    public void stampImageDelete(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");

        response.setContentType("application/json;charset=UTF-8");
        try (var out = response.getWriter()) {
            if (loginuser == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"result\":\"fail\",\"reason\":\"unauth\"}");
                return;
            }
            String root = session.getServletContext().getRealPath("/");
            String path = root + "resources" + File.separator + "stamp_upload";
            signService.deleteStamp(loginuser.getMemberUserid(), path);
            out.print("{\"result\":\"success\"}");
        } catch (IOException ignore) {}
    }

    /* ===================== 결재라인 설정 ===================== */
    @GetMapping("setting/line")
    public String linePopup(@RequestParam(name = "id", required = false) Long id, Model model) {
        if (id != null) model.addAttribute("signline", signlineService.getLinesWithMembers(id));
        return "sign/signlinepopup";
    }

    @PostMapping("lines/{id}/delete")
    @Transactional
    @ResponseBody
    public Map<String, Object> deleteLine(@PathVariable("id") Long id, HttpSession session) {
        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        if (loginuser == null) return Map.of("ok", false, "msg", "로그인이 필요합니다.");

        var opt = signlineRepository.findById(id);
        if (opt.isEmpty()) return Map.of("ok", false, "msg", "존재하지 않는 결재라인입니다.");

        var line = opt.get();
        if (!Objects.equals(line.getFkMemberSeq(), Long.valueOf(loginuser.getMemberSeq())))
            return Map.of("ok", false, "msg", "삭제 권한이 없습니다.");

        signlineRepository.delete(line);
        return Map.of("ok", true);
    }

    @GetMapping("members")
    @ResponseBody
    public List<MemberDTO> members(HttpSession session) {
        MemberDTO login = (MemberDTO) session.getAttribute("loginuser");
        int me = login.getMemberSeq();
        return memberService.getSignlineMember().stream()
                .filter(m -> m.getMemberSeq() != me)
                .toList();
    }

    @PostMapping("lines/write")
    @Transactional
    public void saveLine(
            @RequestParam(name = "lineName") String lineName,
            @RequestParam(name="approverSeq",  required=false) List<Long> approverSeqs,
            @RequestParam(name="approverSeq[]",required=false) List<Long> approverSeqsAlt,
            @RequestParam(name="id", required=false) Long id,
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        if (loginuser == null) {
            response.sendRedirect(request.getContextPath() + "/login/loginStart");
            return;
        }
        if ((approverSeqs == null || approverSeqs.isEmpty()) && approverSeqsAlt != null) {
            approverSeqs = approverSeqsAlt;
        }
        if (lineName == null || lineName.isBlank() || approverSeqs == null || approverSeqs.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/sign/setting");
            return;
        }

        if (id == null) {
            Signline line = Signline.builder()
                    .fkMemberSeq(Long.valueOf(loginuser.getMemberSeq()))
                    .signlineName(lineName)
                    .regdate(java.time.LocalDateTime.now())
                    .build();

            int order = 1;
            for (Long mseq : approverSeqs) {
                line.addMember(SignlineMember.builder()
                        .fkMemberSeq(mseq.intValue())
                        .lineOrder(order++)
                        .build());
            }
            signlineRepository.save(line);
        } else {
            Signline line = signlineRepository.findById(id).orElseThrow();
            line.setSignlineName(lineName);
            line.getMembers().clear();
            int order = 1;
            for (Long mseq : approverSeqs) {
                line.addMember(SignlineMember.builder()
                        .fkMemberSeq(mseq.intValue())
                        .lineOrder(order++)
                        .build());
            }
            signlineRepository.save(line);
        }

        String ctx = request.getContextPath();
        response.setContentType("text/html; charset=UTF-8");
        try (var out = response.getWriter()) {
            out.println("<!DOCTYPE html><html><body><script>");
            out.println("if (window.opener && !window.opener.closed) {");
            out.println("  try { if (window.opener.loadSavedLines) window.opener.loadSavedLines(); } catch(e) {}");
            out.println("  window.close();");
            out.println("} else {");
            out.println("  window.location.replace('" + ctx + "/sign/setting');");
            out.println("}");
            out.println("</script></body></html>");
        }
    }

    @GetMapping("lines")
    @ResponseBody
    public List<SignlineDTO> lines(HttpSession session) {
        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        return signlineService.findAllByOwner(loginuser.getMemberSeq());
    }

    @GetMapping("line/load")
    public String lineLoadPopup() {
        return "sign/signlineloadpopup";
    }

    @GetMapping("lines/{id}")
    @ResponseBody
    public Signline lineDetail(@PathVariable("id") Long id) {
        return signlineService.getLineWithMembers(id);
    }

    /* ===================== 목록(탭) ===================== */
    @GetMapping("inbox")
    public String inbox(HttpSession session, Model model) {
        Long me = Long.valueOf(((MemberDTO)session.getAttribute("loginuser")).getMemberSeq());
        model.addAttribute("rows", signService.buildInboxPreview(me, Integer.MAX_VALUE));
        return "sign/inbox";
    }

    @GetMapping("sent")
    public String sent(HttpSession session, Model model){
        Long me = Long.valueOf(((MemberDTO)session.getAttribute("loginuser")).getMemberSeq());
        model.addAttribute("rows", signService.buildMyDraftboxAll(me));
        return "/sign/sent";
    }

    @GetMapping("history")
    public String history(HttpSession session, Model model) {
        Long me = Long.valueOf(((MemberDTO)session.getAttribute("loginuser")).getMemberSeq());
        model.addAttribute("rows", signService.buildApprovalHistory(me, Integer.MAX_VALUE));
        return "sign/history";
    }

    /* ===================== 상신(4종) ===================== */

    @PostMapping(value="/draft/proposal", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public String submitProposal(
            @RequestParam(name="fk_draft_type_seq") Integer fk_draft_type_seq,
            @RequestParam(name="fk_member_seq") Long fk_member_seq,
            @RequestParam(name="is_emergency", defaultValue="0") Integer isEmergency,
            @RequestParam(name="conform_title") String conform_title,
            @RequestParam(name="conform_content") String conform_content,
            @RequestPart(name="files", required=false) List<MultipartFile> files,
            @RequestParam(name="approverSeq") List<Long> approverSeq,
            @RequestParam(name="lineOrder") List<Integer> lineOrder,
            @RequestParam(name="draft_title", required=false) String draft_title,
            @RequestParam(name="draft_content", required=false) String draft_content,
            HttpSession session
    ){
        Draft draft = signService.createDraft(
                fk_draft_type_seq, fk_member_seq,
                (draft_title != null && !draft_title.isBlank()) ? draft_title : conform_title,
                (draft_content != null && !draft_content.isBlank()) ? draft_content : conform_content,
                isEmergency
        );
        signService.saveBusinessConform(draft.getDraftSeq(), conform_title, conform_content);
        signService.saveApprovalLine(draft, approverSeq, lineOrder);

        String root = session.getServletContext().getRealPath("/");
        signService.saveDraftFiles(
                draft, files,
                root + "resources" + File.separator + "edoc_upload",
                "/resources/edoc_upload"
        );
        return "redirect:/sign/main";
    }

    @PostMapping(value="/draft/vacation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public String submitVacation(
            @RequestParam(name="fk_draft_type_seq") Integer fk_draft_type_seq,
            @RequestParam(name="fk_member_seq") Long fk_member_seq,
            @RequestParam(name="is_emergency", defaultValue="0") Integer isEmergency,
            @RequestParam(name="vacation_title") String vacation_title,
            @RequestParam(name="vacation_start") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate vacation_start,
            @RequestParam(name="vacation_end")   @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate vacation_end,
            @RequestParam(name="vacation_content") String vacation_content,
            @RequestParam(name="vacation_type") String vacation_type, // ANNUAL | HALF
            @RequestParam(name="approverSeq") List<Long> approverSeq,
            @RequestParam(name="lineOrder") List<Integer> lineOrder,
            @RequestParam(name="draft_title", required=false) String draft_title,
            @RequestParam(name="draft_content", required=false) String draft_content,
            @RequestPart(name="files", required=false) List<MultipartFile> files,
            HttpSession session
    ){
        Draft draft = signService.createDraft(
                fk_draft_type_seq, fk_member_seq,
                (draft_title != null && !draft_title.isBlank()) ? draft_title : vacation_title,
                (draft_content != null && !draft_content.isBlank()) ? draft_content : vacation_content,
                isEmergency
        );
        signService.saveVacation(draft.getDraftSeq(), vacation_start, vacation_end, vacation_type, vacation_title, vacation_content);
        signService.saveApprovalLine(draft, approverSeq, lineOrder);

        String root = session.getServletContext().getRealPath("/");
        signService.saveDraftFiles(
                draft, files,
                root + "resources" + File.separator + "edoc_upload",
                "/resources/edoc_upload"
        );
        return "redirect:/sign/main";
    }

    @PostMapping(value="/draft/expense", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public String submitExpense(
            @RequestParam(name="fk_draft_type_seq") Integer fk_draft_type_seq,
            @RequestParam(name="fk_member_seq") Long fk_member_seq,
            @RequestParam(name="is_emergency", defaultValue="0") Integer isEmergency,
            @RequestParam(name="payment_title") String payment_title,
            @RequestParam(name="payment_content") String payment_content,
            @RequestParam(name="payment_list_regdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) List<LocalDate> payment_list_regdate,
            @RequestParam(name="payment_list_content") List<String> uses,
            @RequestParam(name="payment_list_price[]") List<Long> prices,
            @RequestParam(name="total_amount", defaultValue="0") Long total_amount,
            @RequestParam(name="approverSeq") List<Long> approverSeq,
            @RequestParam(name="lineOrder") List<Integer> lineOrder,
            @RequestParam(name="draft_title", required=false) String draft_title,
            @RequestParam(name="draft_content", required=false) String draft_content,
            @RequestPart(name="files", required=false) List<MultipartFile> files,
            HttpSession session
    ){
        Draft draft = signService.createDraft(
                fk_draft_type_seq, fk_member_seq,
                (draft_title != null && !draft_title.isBlank()) ? draft_title : payment_title,
                (draft_content != null && !draft_content.isBlank()) ? draft_content : payment_content,
                isEmergency
        );
        signService.savePayment(draft.getDraftSeq(), payment_title, payment_content,
                total_amount == null ? 0L : total_amount, payment_list_regdate, uses, prices);
        signService.saveApprovalLine(draft, approverSeq, lineOrder);

        String root = session.getServletContext().getRealPath("/");
        signService.saveDraftFiles(
                draft, files,
                root + "resources" + File.separator + "edoc_upload",
                "/resources/edoc_upload"
        );
        return "redirect:/sign/main";
    }

    @PostMapping(value="/draft/trip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public String submitTrip(
            @RequestParam(name="fk_draft_type_seq") Integer fk_draft_type_seq,
            @RequestParam(name="fk_member_seq") Long fk_member_seq,
            @RequestParam(name="is_emergency", defaultValue="0") Integer isEmergency,
            @RequestParam(name="business_title") String business_title,
            @RequestParam(name="business_content") String business_content,
            @RequestParam(name="business_start") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate business_start,
            @RequestParam(name="business_end")   @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate business_end,
            @RequestParam(name="business_location") String business_location,
            @RequestParam(name="business_result") String business_result,
            @RequestParam(name="approverSeq") List<Long> approverSeq,
            @RequestParam(name="lineOrder") List<Integer> lineOrder,
            @RequestParam(name="draft_title", required=false) String draft_title,
            @RequestParam(name="draft_content", required=false) String draft_content,
            @RequestPart(name="files", required=false) List<MultipartFile> files,
            HttpSession session
    ){
        Draft draft = signService.createDraft(
                fk_draft_type_seq, fk_member_seq,
                (draft_title != null && !draft_title.isBlank()) ? draft_title : business_title,
                (draft_content != null && !draft_content.isBlank()) ? draft_content : business_result,
                isEmergency
        );
        signService.saveBusiness(draft.getDraftSeq(), business_title, business_content, business_start, business_end, business_location, business_result);
        signService.saveApprovalLine(draft, approverSeq, lineOrder);

        String root = session.getServletContext().getRealPath("/");
        signService.saveDraftFiles(
                draft, files,
                root + "resources" + File.separator + "edoc_upload",
                "/resources/edoc_upload"
        );
        return "redirect:/sign/main";
    }

    /* ===================== 승인/반려 ===================== */
    @PostMapping("lines/{draftLineSeq}/approve")
    @Transactional
    @ResponseBody
    public Map<String,Object> approve(@PathVariable("draftLineSeq") Long draftLineSeq,
                                      @RequestParam(name="comment", required=false) String comment,
                                      HttpSession session) {
        MemberDTO login = (MemberDTO) session.getAttribute("loginuser");
        Long me = Long.valueOf(login.getMemberSeq());
        var res = signService.approve(draftLineSeq, me, comment);
        Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("ok", res.ok());
        body.put("lineSeq", res.lineSeq());
        body.put("msg", res.msg());
        return body;
    }

    @PostMapping("lines/{draftLineSeq}/reject")
    @Transactional
    @ResponseBody
    public Map<String,Object> reject(@PathVariable("draftLineSeq") Long draftLineSeq,
                                     @RequestParam(name="comment") String comment,
                                     HttpSession session) {
        MemberDTO login = (MemberDTO) session.getAttribute("loginuser");
        Long me = Long.valueOf(login.getMemberSeq());
        var res = signService.reject(draftLineSeq, me, comment);
        Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("ok", res.ok());
        body.put("lineSeq", res.lineSeq());
        body.put("msg", res.msg());
        return body;
    }

    /* ===================== 상세 보기 ===================== */
    @GetMapping("/view/{draftSeq}")
    @Transactional(readOnly = true)
    public String view(@PathVariable("draftSeq") Long draftSeq, HttpSession session, Model model) {
        MemberDTO login = (MemberDTO) session.getAttribute("loginuser");
        if (login == null) throw new IllegalStateException("로그인 필요");

        Long me = Long.valueOf(login.getMemberSeq());
        var v = signService.loadSignView(draftSeq, me);
        model.addAllAttributes(v.toModel());
        return "sign/view";
    }

    /* ===================== 첨부 다운로드 ===================== */
    @GetMapping("files/{id}/download")
    public void downloadFile(@PathVariable("id") Long id,
                             HttpServletRequest request,
                             HttpServletResponse response,
                             HttpSession session) throws IOException {
        String root = session.getServletContext().getRealPath("/");
        signService.streamAttachment(id, root, response);
    }

    /* ===================== 엑셀 다운로드 ===================== */
    @PostMapping("/downloadExcelFile")
    public String downloadExcel(@RequestParam(name = "draftSeq") Long draftSeq, Model model) {
        signService.exportDraftToExcel(draftSeq, model);
        return "excelDownloadView";
    }
}
