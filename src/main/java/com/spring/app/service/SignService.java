package com.spring.app.service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.spring.app.entity.Draft;

import jakarta.servlet.http.HttpServletResponse;

public interface SignService {

    /* ===== Draft 생성/저장 ===== */
    Draft createDraft(int draftTypeSeq, long memberSeq, String title, String content, int isEmergency);

    void saveApprovalLine(Draft draft, List<Long> approverSeq, List<Integer> lineOrder);

    void saveDraftFiles(Draft draft, List<MultipartFile> files, String realUploadDir, String webBasePath);

    /* ===== 기안종류별 서브엔티티 저장 ===== */
    void saveBusinessConform(long draftSeq, String title, String content);

    void saveVacation(long draftSeq, LocalDate start, LocalDate end, String type, String title, String content);

    void savePayment(long draftSeq, String title, String content, long totalAmount,
                     List<LocalDate> dates, List<String> uses, List<Long> prices);

    void saveBusiness(long draftSeq, String title, String content,
                      LocalDate start, LocalDate end, String location, String result);

    /* ===== 목록/요약 ===== */
    List<Map<String,Object>> buildInboxPreview(Long me, int limit);
    List<Map<String, Object>> buildMyDraftboxAll(Long me);
    List<Map<String,Object>> buildSentPreview (Long me, int limit);
    List<Map<String,Object>> buildHistoryPreview(Long me, int limit);
    List<Map<String, Object>> buildApprovalHistory(Long me, int limit);

    /* ===== 상세 뷰 모델 ===== */
    SignView loadSignView(long draftSeq, long me);

    /* ===== 승인/반려(알림, 연차 반영 포함) ===== */
    ApproveResult approve(long draftLineSeq, Long approverSeq, String comment);
    RejectResult  reject (long draftLineSeq, Long approverSeq, String comment);

    /* ===== 도장 업로드/삭제 ===== */
    String saveStamp(String userid, String originalName, InputStream is, String saveDir);
    void   deleteStamp(String userid, String saveDir);

    /* ===== 첨부 다운로드 스트리밍 ===== */
    void streamAttachment(Long draftFileId, String webRootRealPath,
                          HttpServletResponse response) throws java.io.IOException;

    /* ===== 엑셀 다운로드(필요시 간단 구현) ===== */
    void exportDraftToExcel(Long draftSeq, Model model);

    /* ===== 보조 DTO ===== */
    public static record ApproveResult(boolean ok, Long lineSeq, String msg) {}
    public static record RejectResult (boolean ok, Long lineSeq, String msg) {}
    public static record SignView(
            Map<String, Object> modelAttributes
    ) {
        public Map<String, Object> toModel() { return modelAttributes; }
    }
}
