package com.spring.app.service;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.spring.app.chatting.controller.WebsocketEchoHandler;
import com.spring.app.common.FileManager;
import com.spring.app.domain.MemberDTO;
import com.spring.app.entity.*;
import com.spring.app.model.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SignService_imple implements SignService {

    private final FileManager fileManager;
    private final MemberRepository memberRepository;
    private final SignlineRepository signlineRepository;
    private final DraftRepository draftRepository;
    private final DraftLineRepository draftLineRepository;
    private final VacationRepository vacationRepository;
    private final BusinessRepository businessRepository;
    private final BusinessConformRepository businessConformRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentListRepository paymentListRepository;
    private final DraftFileRepository draftFileRepository;

    private final WebsocketEchoHandler wsHandler;

    @PersistenceContext
    private EntityManager em;

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    /* ===================== Draft/라인/첨부 ===================== */
    @Override
    public Draft createDraft(int draftTypeSeq, long memberSeq, String title, String content, int isEmergency) {
        Draft d = Draft.builder()
                .draftType(em.getReference(DraftType.class, (long)draftTypeSeq))
                .member(em.getReference(Member.class, memberSeq))
                .draftTitle(title)
                .draftContent(content)
                .draftStatus(0)
                .isEmergency(isEmergency)
                .build();
        return draftRepository.save(d);
    }

    @Override
    public void saveApprovalLine(Draft draft, List<Long> approverSeq, List<Integer> lineOrder) {
        if (approverSeq == null || approverSeq.isEmpty()) {
            throw new IllegalArgumentException("결재라인이 비어 있습니다.");
        }
        var lines = new ArrayList<DraftLine>();
        for (int i = 0; i < approverSeq.size(); i++) {
            Long approverId = approverSeq.get(i);
            Integer ord = (lineOrder != null && i < lineOrder.size() && lineOrder.get(i) != null)
                    ? lineOrder.get(i) : (i + 1);
            if (ord == null || ord < 1) ord = i + 1;

            lines.add(DraftLine.builder()
                    .draft(draft)
                    .approver(em.getReference(Member.class, approverId))
                    .lineOrder(ord)
                    .signStatus(0)
                    .build());
        }
        draftLineRepository.saveAll(lines);
    }

    @Override
    public void saveDraftFiles(Draft draft, List<MultipartFile> files, String realUploadDir, String webBasePath) {
        if (files == null || files.isEmpty()) return;

        File dir = new File(realUploadDir);
        if (!dir.exists()) dir.mkdirs();

        List<DraftFile> list = new ArrayList<>();
        for (MultipartFile mf : files) {
            if (mf == null || mf.isEmpty()) continue;
            try {
                String original = mf.getOriginalFilename();
                String saved = fileManager.doFileUpload(mf.getBytes(), original, realUploadDir);
                DraftFile df = DraftFile.builder()
                        .draft(draft)
                        .fileName(original)
                        .filePath(ensureTrailingSlash(webBasePath) + saved)
                        .build();
                list.add(df);
            } catch (Exception ignore) {}
        }
        if (!list.isEmpty()) draftFileRepository.saveAll(list);
    }

    private static String ensureTrailingSlash(String p) {
        if (p == null || p.isBlank()) return "/";
        return p.endsWith("/") ? p : (p + "/");
    }

    /* ===================== 서브엔티티 저장 ===================== */
    @Override
    public void saveBusinessConform(long draftSeq, String title, String content) {
        businessConformRepository.save(
                BusinessConform.builder()
                        .draftSeq(draftSeq)
                        .conformTitle(title)
                        .conformContent(content)
                        .build()
        );
    }

    @Override
    public void saveVacation(long draftSeq, LocalDate start, LocalDate end, String type, String title, String content) {
        vacationRepository.save(
                Vacation.builder()
                        .draftSeq(draftSeq)
                        .vacationTitle(title)
                        .vacationType(type)
                        .vacationStart(start)
                        .vacationEnd(end)
                        .vacationContent(content)
                        .build()
        );
    }

    @Override
    public void savePayment(long draftSeq, String title, String content, long totalAmount,
                            List<LocalDate> dates, List<String> uses, List<Long> prices) {
        paymentRepository.save(
                Payment.builder()
                        .draftSeq(draftSeq)
                        .paymentTitle(title)
                        .paymentContent(content)
                        .totalAmount(totalAmount)
                        .build()
        );
        int n = Math.max(dates.size(), Math.max(uses.size(), prices.size()));
        List<PaymentList> rows = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            LocalDate d = i < dates.size() ? dates.get(i) : null;
            String    u = i < uses.size()  ? uses.get(i)  : null;
            Long      p = i < prices.size()? prices.get(i): 0L;
            if (d == null && (u == null || u.isBlank()) && (p == null || p == 0L)) continue;
            rows.add(
                    PaymentList.builder()
                            .fkDraftSeq(draftSeq)
                            .regdate(d == null ? LocalDate.now() : d)
                            .content(u)
                            .price(p == null ? 0L : p)
                            .build()
            );
        }
        if (!rows.isEmpty()) paymentListRepository.saveAll(rows);
    }

    @Override
    public void saveBusiness(long draftSeq, String title, String content,
                             LocalDate start, LocalDate end, String location, String result) {
        businessRepository.save(
                Business.builder()
                        .draftSeq(draftSeq)
                        .businessTitle(title)
                        .businessContent(content)
                        .businessStart(start)
                        .businessEnd(end)
                        .businessLocation(location)
                        .businessResult(result)
                        .build()
        );
    }

    /* ===================== 요약/목록 ===================== */
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> buildInboxPreview(Long me, int limit) {
        var rows = draftLineRepository.findInbox(me);

        List<Map<String,Object>> preview = new ArrayList<>(Math.min(limit, rows.size()));
        for (var dl : rows) {
            var d = dl.getDraft();

            Map<String,Object> m = new LinkedHashMap<>();
            m.put("draftSeq", d.getDraftSeq());
            m.put("title", d.getDraftTitle());
            m.put("docType", d.getDraftType()!=null ? d.getDraftType().getDraftTypeName() : "-");
            m.put("drafterName", d.getMember()!=null ? d.getMember().getMemberName() : "-");
            m.put("isEmergency", d.getIsEmergency()==null ? 0 : d.getIsEmergency());
            m.put("regdate", toDate(d.getDraftRegdate()));  // fmt:formatDate로 포맷
            preview.add(m);

            if (preview.size() == limit) break;
        }
        return preview;
    }
    
 // SignService_imple

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> buildMyDraftboxAll(Long me) {
        // 내가 올린 모든 문서(문서함 전체)
        var drafts = draftRepository.findByMemberWithType(me);

        List<Map<String,Object>> rows = new ArrayList<>(drafts.size());
        for (Draft d : drafts) {
            var lines = draftLineRepository
                    .findByDraft_DraftSeqOrderByLineOrderAscDraftLineSeqAsc(d.getDraftSeq());

            boolean anyReject  = lines.stream().anyMatch(l -> Integer.valueOf(9).equals(l.getSignStatus()));
            boolean allApprove = !lines.isEmpty() &&
                                 lines.stream().allMatch(l -> Integer.valueOf(1).equals(l.getSignStatus()));
            int status = anyReject ? 9 : (allApprove ? 1 : 0);  // 0 진행중, 1 완료, 9 반려

            // 완료일(있으면)
            Date signDate = lines.stream()
                    .filter(l -> Integer.valueOf(1).equals(l.getSignStatus()) && l.getSignDate()!=null)
                    .map(l -> Date.from(l.getSignDate().atZone(ZONE).toInstant()))
                    .max(Date::compareTo)
                    .orElse(null);

            Map<String,Object> m = new LinkedHashMap<>();
            m.put("draftSeq", d.getDraftSeq());
            m.put("title", d.getDraftTitle());
            m.put("docType", d.getDraftType()!=null ? d.getDraftType().getDraftTypeName() : "-");
            m.put("regdate", toDate(d.getDraftRegdate()));
            m.put("isEmergency", d.getIsEmergency()==null ? 0 : d.getIsEmergency());
            m.put("status", status);   // JSP status-0/1/9 사용 가능
            m.put("signDate", signDate);
            rows.add(m);
        }

        // 최신 작성일 기준 내림차순
        rows.sort((a,b) -> {
            Date da = (Date)a.get("regdate");
            Date db = (Date)b.get("regdate");
            if (da == null && db == null) return 0;
            if (da == null) return 1;
            if (db == null) return -1;
            return db.compareTo(da);
        });
        return rows;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> buildSentPreview(Long me, int limit) {
        // ✅ 문서함: 내가 올린 "모든" 문서를 가져와서
        var drafts = draftRepository.findByMemberWithType(me);

        // 완료가 아닌 문서만 = 결재 진행 문서 (승인 0건, 승인 일부, 반려 포함)
        List<Map<String,Object>> out = new ArrayList<>();

        for (Draft d : drafts) {
            var lines = draftLineRepository
                    .findByDraft_DraftSeqOrderByLineOrderAscDraftLineSeqAsc(d.getDraftSeq());

            boolean anyReject  = lines.stream().anyMatch(l -> Integer.valueOf(9).equals(l.getSignStatus()));
            boolean allApprove = !lines.isEmpty()
                    && lines.stream().allMatch(l -> Integer.valueOf(1).equals(l.getSignStatus()));

            int status = anyReject ? 9 : (allApprove ? 1 : 0);

            // 완료(=allApprove)면 이 메서드에서는 제외
            if (allApprove) continue;

            Map<String,Object> m = new LinkedHashMap<>();
            m.put("draftSeq", d.getDraftSeq());
            m.put("docType", d.getDraftType()!=null ? d.getDraftType().getDraftTypeName() : "-");
            m.put("title", d.getDraftTitle());
            m.put("regdate", toDate(d.getDraftRegdate()));
            m.put("isEmergency", d.getIsEmergency()==null ? 0 : d.getIsEmergency());
            m.put("status", status); // JSP의 status-0/1/9
            out.add(m);

            if (out.size() == limit) break;
        }

        // 최신순 정렬(작성일 기준)
        out.sort((a,b) -> {
            Date da = (Date)a.get("regdate");
            Date db = (Date)b.get("regdate");
            if (da == null && db == null) return 0;
            if (da == null) return 1;
            if (db == null) return -1;
            return db.compareTo(da);
        });
        return out;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> buildHistoryPreview(Long me, int limit) {
        // ✅ 문서함: 내가 올린 "모든" 문서 중 완료만
        var drafts = draftRepository.findByMemberWithType(me);

        List<Map<String,Object>> out = new ArrayList<>();

        for (Draft d : drafts) {
            var lines = draftLineRepository
                    .findByDraft_DraftSeqOrderByLineOrderAscDraftLineSeqAsc(d.getDraftSeq());

            boolean allApprove = !lines.isEmpty()
                    && lines.stream().allMatch(l -> Integer.valueOf(1).equals(l.getSignStatus()));
            if (!allApprove) continue;

            // 완료 시각 = 승인된 라인의 signDate 중 최댓값 (없으면 regdate로 fallback)
            Date signDate = lines.stream()
                    .filter(l -> Integer.valueOf(1).equals(l.getSignStatus()) && l.getSignDate()!=null)
                    .map(l -> Date.from(l.getSignDate().atZone(ZONE).toInstant()))
                    .max(Date::compareTo)
                    .orElse(toDate(d.getDraftRegdate()));

            Map<String,Object> m = new LinkedHashMap<>();
            m.put("draftSeq", d.getDraftSeq());
            m.put("title", d.getDraftTitle());
            m.put("docType", d.getDraftType()!=null ? d.getDraftType().getDraftTypeName() : "문서");
            m.put("drafterName", d.getMember()!=null ? d.getMember().getMemberName() : "-");
            m.put("signDate", signDate);
            out.add(m);

            if (out.size() == limit) break;
        }

        // 완료일 내림차순
        out.sort((a,b) -> {
            Date da = (Date)a.get("signDate");
            Date db = (Date)b.get("signDate");
            if (da == null && db == null) return 0;
            if (da == null) return 1;
            if (db == null) return -1;
            return db.compareTo(da);
        });
        return out;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> buildApprovalHistory(Long me, int limit) {
        var rows = draftLineRepository.findHistory(me); // 내가 승인/반려했던 라인들

        List<Map<String,Object>> out = new ArrayList<>(Math.min(limit, rows.size()));
        for (var dl : rows) {
            var d = dl.getDraft();

            Map<String,Object> m = new LinkedHashMap<>();
            m.put("draftSeq", d.getDraftSeq());
            m.put("title", d.getDraftTitle());
            m.put("docType", d.getDraftType()!=null ? d.getDraftType().getDraftTypeName() : "문서");
            m.put("drafterName", d.getMember()!=null ? d.getMember().getMemberName() : "-");
            m.put("signDate", dl.getSignDate()==null ? null
                    : java.util.Date.from(dl.getSignDate().atZone(ZONE).toInstant()));
            m.put("myStatus", dl.getSignStatus()==null ? 0 : dl.getSignStatus()); // 1=승인, 9=반려
            out.add(m);
            if (out.size() == limit) break;
        }
        return out;
    }

    /* 공통 유틸 */
    private static Date toDate(java.time.LocalDateTime ldt) {
        return ldt == null ? null : Date.from(ldt.atZone(ZONE).toInstant());
    }


    /* ===================== 상세 뷰 모델 ===================== */
    @Override
    @Transactional(readOnly = true)
    public SignView loadSignView(long draftSeq, long me) {
        // 1) 결재라인(안정적인 표시를 위해 정렬)
        List<DraftLine> lines = draftLineRepository.findLinesWithApprover(draftSeq);
        if (lines.isEmpty()) throw new IllegalArgumentException("결재라인이 존재하지 않습니다. draftSeq=" + draftSeq);
        lines.sort(java.util.Comparator.comparing(DraftLine::getLineOrder)); // 1,2,3,... (표시는 JSP에서 우->좌)

        // 2) 본문
        Draft draft = em.createQuery("""
            select d
              from Draft d
              join fetch d.member m
              left join fetch m.department
              left join fetch m.grade
              left join fetch d.draftType
             where d.draftSeq = :id
        """, Draft.class)
        .setParameter("id", draftSeq)
        .getSingleResult();

        // 3) 내 라인
        DraftLine myLine = draftLineRepository
                .findMyLine(Long.valueOf(draftSeq), Long.valueOf(me))
                .orElse(null);

        // ★ 변경: 다음 순번 = '대기(0)' 중 가장 큰 lineOrder (3→2→1 순서)
        Integer nextOrd = lines.stream()
                .filter(dl -> Integer.valueOf(0).equals(dl.getSignStatus()))
                .map(DraftLine::getLineOrder)
                .max(Integer::compareTo)
                .orElse(null);

        boolean canActNow = (myLine != null)
                && Integer.valueOf(0).equals(myLine.getSignStatus())
                && java.util.Objects.equals(myLine.getLineOrder(), nextOrd);

        boolean canEdit = (myLine != null);

        String docTypeName = (draft.getDraftType() != null && draft.getDraftType().getDraftTypeName() != null)
                ? draft.getDraftType().getDraftTypeName() : "문서";

        // 4) 내 도장
        String myStamp = "";
        try {
            String loginUserid = memberRepository.findById((int) me)
                    .map(Member::getMemberUserid)
                    .orElse(null);
            if (loginUserid != null && !loginUserid.isBlank()) {
                String saved = memberRepository.findStampImageByUserid(loginUserid);
                if (saved != null) myStamp = saved;
            }
        } catch (Exception ignore) {}

        // 5) 첨부 목록
        List<DraftFile> attachments =
                draftFileRepository.findByDraft_DraftSeqOrderByDraftFileSeqAsc(draftSeq);

        // 6) 모델 구성
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("draft", draft);
        model.put("lines", lines);
        model.put("canAct", canActNow);
        model.put("canEdit", canEdit);
        model.put("myDraftLineSeq", (myLine != null ? myLine.getDraftLineSeq() : null));
        model.put("loginMemberSeq", me);
        model.put("docTypeName", docTypeName);
        model.put("myStampImage", myStamp);
        model.put("attachments", attachments);

        // 서브 엔티티
        vacationRepository.findByDraftSeq(draftSeq).ifPresent(v -> model.put("vacation", v));
        paymentRepository.findByDraftSeq(draftSeq).ifPresent(p -> {
            model.put("payment", p);
            List<PaymentList> items =
                    paymentListRepository.findByFkDraftSeqOrderByRegdateAscPaymentListSeqAsc(draftSeq);
            model.put("paymentLists", items);
        });

        // ★ 추가: 출장(보고서) — JSP에서 ${business.*}로 사용
        businessRepository.findByDraftSeq(draftSeq).ifPresent(b -> model.put("business", b));

        return new SignView(model);
    }



    /* ===================== 승인/반려 ===================== */
    @Override
    public ApproveResult approve(long draftLineSeq, Long approverSeq, String comment) {
        DraftLine anyLine = draftLineRepository.findById(draftLineSeq).orElseThrow();
        Long draftSeq = anyLine.getDraft().getDraftSeq();

        DraftLine mine = draftLineRepository.findMyLine(draftSeq, approverSeq).orElse(null);
        if (mine == null) return new ApproveResult(false, null, "내 결재선이 아닙니다.");

        Draft d = anyLine.getDraft();
        int prevStatus = (d.getDraftStatus()==null ? 0 : d.getDraftStatus());

        boolean editing = (mine.getSignStatus()!=null && mine.getSignStatus()!=0);
        if (!editing) {
            Integer nextOrd = draftLineRepository.findNextOrder(draftSeq);
            if (nextOrd == null || !mine.getLineOrder().equals(nextOrd)) {
                return new ApproveResult(false, null, "지금은 결재할 수 없는 상태입니다.");
            }
        }

        Integer oldStatus  = mine.getSignStatus();
        String  oldComment = mine.getSignComment();

        mine.setSignStatus(1);
        mine.setSignComment(comment);
        mine.setSignDate(java.time.LocalDateTime.now());
        draftLineRepository.save(mine);

        List<DraftLine> lines = draftLineRepository
                .findByDraft_DraftSeqOrderByLineOrderAscDraftLineSeqAsc(draftSeq);
        boolean anyReject  = lines.stream().anyMatch(l -> Integer.valueOf(9).equals(l.getSignStatus()));
        boolean allApprove = !anyReject && lines.stream().allMatch(l -> Integer.valueOf(1).equals(l.getSignStatus()));

        d.setDraftStatus(anyReject ? 9 : (allApprove ? 1 : 0));
        draftRepository.save(d);

        if (prevStatus == 1 && !allApprove) {
            revertVacationDeductionIfNeeded(d);
        } else if (prevStatus != 1 && allApprove) {
            applyVacationDeductionIfNeeded(d);
        }

        // 알림
        try {
            String link = "/sign/view/" + draftSeq;
            String docTitle = d.getDraftTitle();
            String approverName = getMemberName(approverSeq);
            boolean edited = (oldStatus != null && oldStatus != 0);
            boolean commentChanged = !Objects.equals(oldComment, comment);

            if (allApprove) {
                pushToUser(d.getMember(),
                        (edited && oldStatus == 9) ? "전자결재 최종 승인(반려→승인)" : "전자결재 최종 승인",
                        "「" + docTitle + "」 문서가 최종 승인되었습니다.",
                        link,
                        "appr_final_" + draftSeq
                );
            } else {
                String title = (edited && oldStatus == 9) ? "전자결재 승인(반려→승인)" : "전자결재 승인";
                pushToUser(d.getMember(),
                        title,
                        approverName + " 님이 「" + docTitle + "」 문서를 승인했습니다.",
                        link,
                        "appr_step_" + draftSeq + "_" + mine.getLineOrder()
                );
                if (edited && oldStatus == 1 && commentChanged) {
                    pushToUser(d.getMember(),
                            "결재 의견 수정",
                            approverName + " 님이 「" + docTitle + "」 의견을 수정했습니다.",
                            link,
                            "appr_comment_update_" + draftSeq + "_" + mine.getLineOrder()
                    );
                }
            }

            Integer nextOrdAfter = draftLineRepository.findNextOrder(draftSeq);
            if (!allApprove && nextOrdAfter != null) {
                DraftLine nextLine = lines.stream()
                        .filter(l -> nextOrdAfter.equals(l.getLineOrder()))
                        .findFirst().orElse(null);
                if (nextLine != null && nextLine.getApprover() != null) {
                    pushToUser(nextLine.getApprover(),
                            (edited && oldStatus == 9) ? "결재 요청 도착(재개)" : "결재 요청 도착",
                            "「" + docTitle + "」 결재 대기 (" + nextOrdAfter + "단계)",
                            link,
                            "req_" + draftSeq + "_" + nextOrdAfter
                    );
                }
            }
        } catch (Exception ignore) {}

        return new ApproveResult(true, mine.getDraftLineSeq(), null);
    }

    @Override
    public RejectResult reject(long draftLineSeq, Long approverSeq, String comment) {
        DraftLine anyLine = draftLineRepository.findById(draftLineSeq).orElseThrow();
        Long draftSeq = anyLine.getDraft().getDraftSeq();

        DraftLine mine = draftLineRepository.findMyLine(draftSeq, approverSeq).orElse(null);
        if (mine == null) return new RejectResult(false, null, "내 결재선이 아닙니다.");

        Draft d = anyLine.getDraft();
        int prevStatus = (d.getDraftStatus()==null ? 0 : d.getDraftStatus());

        boolean editing = (mine.getSignStatus()!=null && mine.getSignStatus()!=0);
        if (!editing) {
            Integer nextOrd = draftLineRepository.findNextOrder(draftSeq);
            if (nextOrd == null || !mine.getLineOrder().equals(nextOrd)) {
                return new RejectResult(false, null, "지금은 결재할 수 없는 상태입니다.");
            }
        }

        Integer oldStatus  = mine.getSignStatus();
        String  oldComment = mine.getSignComment();

        mine.setSignStatus(9);
        mine.setSignComment(comment);
        mine.setSignDate(java.time.LocalDateTime.now());
        draftLineRepository.save(mine);

        List<DraftLine> lines = draftLineRepository
                .findByDraft_DraftSeqOrderByLineOrderAscDraftLineSeqAsc(draftSeq);
        boolean anyReject  = lines.stream().anyMatch(l -> Integer.valueOf(9).equals(l.getSignStatus()));
        boolean allApprove = !anyReject && lines.stream().allMatch(l -> Integer.valueOf(1).equals(l.getSignStatus()));

        d.setDraftStatus(anyReject ? 9 : (allApprove ? 1 : 0));
        draftRepository.save(d);

        if (prevStatus == 1 && !allApprove) {
            revertVacationDeductionIfNeeded(d);
        }

        try {
            String link = "/sign/view/" + draftSeq;
            String docTitle = d.getDraftTitle();
            String approverName = getMemberName(approverSeq);
            boolean edited = (oldStatus != null && oldStatus != 0);
            boolean commentChanged = !Objects.equals(oldComment, comment);

            String reason = (comment == null || comment.isBlank()) ? ""
                    : (" (사유: " + (comment.length() > 60 ? comment.substring(0,60) + "…" : comment) + ")");

            String title;
            String body;
            String notiId;
            if (edited && oldStatus == 1) {
                title = "전자결재 반려(승인→반려)";
                body  = approverName + " 님이 「" + docTitle + "」 문서를 승인에서 반려로 변경했습니다." + reason;
                notiId = "reject_change_" + draftSeq;
            } else if (edited && oldStatus == 9 && commentChanged) {
                title = "전자결재 반려 의견 수정";
                body  = approverName + " 님이 「" + docTitle + "」 반려 의견을 수정했습니다." + reason;
                notiId = "reject_update_" + draftSeq;
            } else {
                title = "전자결재 반려";
                body  = approverName + " 님이 「" + docTitle + "」 문서를 반려했습니다." + reason;
                notiId = "reject_" + draftSeq;
            }

            pushToUser(d.getMember(), title, body, link, notiId);

            if (edited && oldStatus == 1) {
                DraftLine nextCandidate = lines.stream()
                        .filter(l -> Integer.valueOf(0).equals(l.getSignStatus()))
                        .sorted(Comparator.comparing(DraftLine::getLineOrder))
                        .findFirst().orElse(null);
                if (nextCandidate != null && nextCandidate.getApprover() != null) {
                    pushToUser(nextCandidate.getApprover(),
                            "결재 요청 취소",
                            "「" + docTitle + "」 결재 요청이 상위 단계에서 반려되어 취소되었습니다.",
                            link,
                            "req_cancel_" + draftSeq + "_" + nextCandidate.getLineOrder()
                    );
                }
            }
        } catch (Exception ignore) {}

        return new RejectResult(true, mine.getDraftLineSeq(), null);
    }

    private void applyVacationDeductionIfNeeded(Draft draft) {
        var optVac = vacationRepository.findByDraftSeq(draft.getDraftSeq());
        if (optVac.isEmpty()) return;
        var v = optVac.get();
        BigDecimal useDays = calcVacationDays(v);

        em.createNativeQuery(
                "UPDATE TBL_ANNUAL_LEAVE " +
                        "   SET USED_LEAVE = USED_LEAVE + :d, " +
                        "       REMAINING_LEAVE = REMAINING_LEAVE - :d " +
                        " WHERE MEMBER_SEQ = :m"
        ).setParameter("d", useDays)
         .setParameter("m", draft.getMember().getMemberSeq())
         .executeUpdate();
    }

    private void revertVacationDeductionIfNeeded(Draft draft) {
        var optVac = vacationRepository.findByDraftSeq(draft.getDraftSeq());
        if (optVac.isEmpty()) return;
        var v = optVac.get();
        BigDecimal useDays = calcVacationDays(v);

        em.createNativeQuery(
                "UPDATE TBL_ANNUAL_LEAVE " +
                        "   SET USED_LEAVE = USED_LEAVE - :d, " +
                        "       REMAINING_LEAVE = REMAINING_LEAVE + :d " +
                        " WHERE MEMBER_SEQ = :m"
        ).setParameter("d", useDays)
         .setParameter("m", draft.getMember().getMemberSeq())
         .executeUpdate();
    }

    private BigDecimal calcVacationDays(Vacation v) {
        if ("HALF".equalsIgnoreCase(v.getVacationType())) return new BigDecimal("0.5");
        long days = java.time.temporal.ChronoUnit.DAYS.between(v.getVacationStart(), v.getVacationEnd()) + 1;
        return new BigDecimal(days);
    }

    /* ===================== 도장 파일 ===================== */
    @Override
    public String saveStamp(String userid, String originalName, InputStream is, String saveDir) {
        try {
            File dir = new File(saveDir);
            if (!dir.exists()) dir.mkdirs();
            String newFilename = fileManager.doFileUpload(is, originalName, saveDir);
            memberRepository.stampImageSave(userid, newFilename);
            return newFilename;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteStamp(String userid, String saveDir) {
        String savedFilename = memberRepository.findStampImageByUserid(userid);
        if (savedFilename != null && !savedFilename.isBlank()) {
            try { fileManager.doFileDelete(savedFilename, saveDir); } catch (Exception ignore) {}
        }
        memberRepository.clearStampImageByUserid(userid);
    }

    /* ===================== 첨부 다운로드 스트리밍 ===================== */
    @Override
    @Transactional(readOnly = true)
    public void streamAttachment(Long draftFileId, String webRootRealPath,
                                 HttpServletResponse response) throws java.io.IOException {
        DraftFile f = draftFileRepository.findById(draftFileId).orElse(null);
        if (f == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String webPath = f.getFilePath(); // e.g. /resources/edoc_upload/uuid.ext
        String real = webPath.startsWith("/") ? webRootRealPath + webPath.substring(1) : webRootRealPath + webPath;

        File file = new File(real);
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType("application/octet-stream");
        String encoded = URLEncoder.encode(f.getFileName(), StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encoded + "\"; filename*=UTF-8''" + encoded);
        response.setContentLengthLong(file.length());

        java.nio.file.Files.copy(file.toPath(), response.getOutputStream());
        response.getOutputStream().flush();
    }

    /* ===================== 엑셀 다운로드(간단 버전) ===================== */
    @Override
    @Transactional(readOnly = true)
    public void exportDraftToExcel(Long draftSeq, Model model) {
        // === 1) 도메인 조회(기존 로직 유지) ===
        Draft draft = draftRepository.findById(draftSeq).orElseThrow();
        List<DraftLine> lines = draftLineRepository
                .findByDraft_DraftSeqOrderByLineOrderAscDraftLineSeqAsc(draftSeq);
        model.addAttribute("draft", draft);
        model.addAttribute("lines", lines);
        vacationRepository.findByDraftSeq(draftSeq).ifPresent(v -> model.addAttribute("vacation", v));
        paymentRepository.findByDraftSeq(draftSeq).ifPresent(p -> {
            model.addAttribute("payment", p);
            List<PaymentList> items =
                    paymentListRepository.findByFkDraftSeqOrderByRegdateAscPaymentListSeqAsc(draftSeq);
            model.addAttribute("paymentLists", items);
        });
        businessRepository.findByDraftSeq(draftSeq).ifPresent(b -> model.addAttribute("business", b));
        businessConformRepository.findByDraftSeq(draftSeq).ifPresent(c -> model.addAttribute("conform", c));

        // === 2) 워크북/시트 생성(디자인 포함) ===
        org.apache.poi.xssf.streaming.SXSSFWorkbook wb = new org.apache.poi.xssf.streaming.SXSSFWorkbook();
        var sheet = wb.createSheet("전자결재");

        // 컬럼 폭(문자폭 * 256)
        int[] widths = { 10, 18, 40, 38, 20 };
        for (int i = 0; i < widths.length; i++) sheet.setColumnWidth(i, widths[i] * 256);

        // --- 폰트
        var fontTitle = wb.createFont();
        fontTitle.setBold(true); fontTitle.setFontHeightInPoints((short)14);

        var fontBold  = wb.createFont();
        fontBold.setBold(true);

        var fontNormal = wb.createFont();

        // --- 스타일 헬퍼
        java.util.function.Function<Boolean, org.apache.poi.ss.usermodel.CellStyle> boxStyle =
                (Boolean label) -> {
                    var cs = wb.createCellStyle();
                    cs.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
                    cs.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
                    cs.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
                    cs.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
                    cs.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
                    if (Boolean.TRUE.equals(label)) {
                        cs.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.getIndex());
                        cs.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
                        cs.setFont(fontBold);
                        cs.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
                    } else {
                        cs.setFont(fontNormal);
                    }
                    return cs;
                };

        var titleStyle = wb.createCellStyle();
        titleStyle.setFont(fontTitle);
        titleStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        titleStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.getIndex());
        titleStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

        var sectionStyle = wb.createCellStyle();
        sectionStyle.setFont(fontBold);
        sectionStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.LEFT);
        sectionStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        sectionStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.getIndex());
        sectionStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        sectionStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        sectionStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        sectionStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        sectionStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

        var headerStyle = wb.createCellStyle();
        headerStyle.setFont(fontBold);
        headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

        var cellLeft = wb.createCellStyle();
        cellLeft.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        cellLeft.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        cellLeft.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        cellLeft.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        cellLeft.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        cellLeft.setWrapText(true);

        var cellCenter = wb.createCellStyle();
        cellCenter.cloneStyleFrom(cellLeft);
        cellCenter.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

        var cellDate = wb.createCellStyle();
        cellDate.cloneStyleFrom(cellLeft);
        var df = wb.createDataFormat();
        cellDate.setDataFormat(df.getFormat("yyyy-mm-dd hh:mm"));

        // --- 좌표 유틸
        java.util.function.BiConsumer<Integer, String> mergeAcrossToE = (rowIdx, value) -> {
            var row = sheet.createRow(rowIdx);
            var c0 = row.createCell(0); c0.setCellValue(value); c0.setCellStyle(sectionStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowIdx, rowIdx, 0, 4));
        };

        int r = 0;

        // === 2-1) 타이틀(병합 A1:E1)
        var titleRow = sheet.createRow(r++);
        titleRow.setHeightInPoints(26);
        var cellTitle = titleRow.createCell(0);
        String docTitle = (draft.getDraftTitle() == null || draft.getDraftTitle().isBlank())
                ? "전자결재"
                : ("전자결재 - " + draft.getDraftTitle());
        cellTitle.setCellValue(docTitle);
        cellTitle.setCellStyle(titleStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 4));

        r++; // 빈 줄

        // === 2-2) 상단 정보 박스 (라벨/값 병합)
        String drafterName = (draft.getMember() == null) ? "-" :
                (draft.getMember().getMemberName() == null ? "-" : draft.getMember().getMemberName());
        String typeName = (draft.getDraftType() == null || draft.getDraftType().getDraftTypeName() == null)
                ? "-" : draft.getDraftType().getDraftTypeName();
        String emergency = (draft.getIsEmergency() != null && draft.getIsEmergency() == 1) ? "긴급" : "-";
        String regWhen = (draft.getDraftRegdate() == null) ? ""
                : java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                  .format(draft.getDraftRegdate().atZone(java.time.ZoneId.of("Asia/Seoul")));

        // 라벨/값 한 줄씩, 값은 B~E 병합
        String[][] box = new String[][]{
                {"문서번호", String.valueOf(draft.getDraftSeq())},
                {"제목", (draft.getDraftTitle() == null ? "" : draft.getDraftTitle())},
                {"기안자", drafterName},
                {"유형", typeName},
                {"긴급", emergency},
                {"작성일", regWhen}
        };
        for (String[] row : box) {
            var rr = sheet.createRow(r++);
            var l = rr.createCell(0); l.setCellValue(row[0]); l.setCellStyle(boxStyle.apply(true));
            var v = rr.createCell(1); v.setCellValue(row[1]); v.setCellStyle(boxStyle.apply(false));
            rr.createCell(2).setCellStyle(boxStyle.apply(false));
            rr.createCell(3).setCellStyle(boxStyle.apply(false));
            rr.createCell(4).setCellStyle(boxStyle.apply(false));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rr.getRowNum(), rr.getRowNum(), 1, 4));
        }

        r++; // 빈 줄

        // === 2-3) 섹션 헤더: 결재 라인
        mergeAcrossToE.accept(r++, "결재 라인");

        // === 2-4) 테이블 헤더
        var th = sheet.createRow(r++);
        String[] headers = {"순서", "결재자", "상태", "의견", "결재일시"};
        for (int c = 0; c < headers.length; c++) {
            var hc = th.createCell(c);
            hc.setCellValue(headers[c]);
            hc.setCellStyle(headerStyle);
        }

        // === 2-5) 라인 데이터
        java.time.ZoneId ZONE = java.time.ZoneId.of("Asia/Seoul");
        for (var dl : lines) {
            var row = sheet.createRow(r++);
            // 순서
            var c0 = row.createCell(0); c0.setCellValue(dl.getLineOrder() == null ? 0 : dl.getLineOrder()); c0.setCellStyle(cellCenter);
            // 결재자
            String approver = (dl.getApprover() == null) ? "-" :
                    (dl.getApprover().getMemberName() == null ? "-" : dl.getApprover().getMemberName());
            var c1 = row.createCell(1); c1.setCellValue(approver); c1.setCellStyle(cellLeft);
            // 상태
            int st = (dl.getSignStatus() == null ? 0 : dl.getSignStatus());
            String stTxt = (st == 1 ? "승인" : (st == 9 ? "반려" : "대기"));
            var c2 = row.createCell(2); c2.setCellValue(stTxt); c2.setCellStyle(cellCenter);
            // 의견
            var c3 = row.createCell(3); c3.setCellValue(dl.getSignComment() == null ? "" : dl.getSignComment()); c3.setCellStyle(cellLeft);
            // 결재일시
            var c4 = row.createCell(4); c4.setCellStyle(cellDate);
            if (dl.getSignDate() != null) {
                java.util.Date signedAt = java.util.Date.from(dl.getSignDate().atZone(ZONE).toInstant());
                c4.setCellValue(signedAt);
            } else {
                c4.setCellValue("");
            }
            // 가독성을 위해 의견 줄 높이 조금
            row.setHeightInPoints(22);
        }

        r++; // 빈 줄

        // === 2-6) (선택) 본문 섹션
        mergeAcrossToE.accept(r++, "본문");
        var bodyRow = sheet.createRow(r++);
        var bodyCell = bodyRow.createCell(0);
        bodyCell.setCellValue(draft.getDraftContent() == null ? "" : draft.getDraftContent());
        bodyCell.setCellStyle(cellLeft);
        // 본문은 A~E 병합
        for (int c = 1; c <= 4; c++) bodyRow.createCell(c).setCellStyle(cellLeft);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(bodyRow.getRowNum(), bodyRow.getRowNum(), 0, 4));
        bodyRow.setHeightInPoints(60);

        // === 3) 파일명 앞부분(workbookName) + locale ===
        String baseName = (draft.getDraftTitle() != null && !draft.getDraftTitle().isBlank())
                ? draft.getDraftTitle() : ("전자결재_" + draftSeq);
        baseName = baseName.replaceAll("[\\\\/:*?\"<>|]", "_");

        model.addAttribute("workbook", wb);                    // SXSSFWorkbook (ExcelDownloadView가 write/close)
        model.addAttribute("workbookName", baseName);
        model.addAttribute("locale", java.util.Locale.KOREA);
    }


    /* ===================== 푸시 알림 ===================== */
    private String getMemberName(Long memberSeq) {
        if (memberSeq == null) return "-";
        return memberRepository.findById(memberSeq.intValue())
                .map(Member::getMemberName)
                .orElse("-");
    }

    private void pushToUser(Member target, String title, String body, String link, String notiId) {
        if (target == null) return;

        String userid = null;
        try {
            userid = (String) Member.class.getMethod("getMemberUserid").invoke(target);
        } catch (Exception ignore) {
            try { userid = (String) Member.class.getMethod("getMemberId").invoke(target); }
            catch (Exception ignore2) {}
        }
        if (userid == null || userid.isBlank()) return;

        wsHandler.pushNotify(userid,
                new WebsocketEchoHandler.NotifyPayload(
                        "notify",
                        title,
                        body,
                        link,
                        notiId,
                        java.time.OffsetDateTime.now().toString()
                )
        );
    }
}
