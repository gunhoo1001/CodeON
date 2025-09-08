package com.spring.app.domain;

import lombok.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

@Getter @Setter @NoArgsConstructor @Builder
public class AttendanceRowView {
    private LocalDate      workDate;
    private String         memberName;
    private Integer        memberSeq;
    private LocalDateTime  startTime;
    private LocalDateTime  endTime;
    private Integer        overtimeMinutes;   // DB OVERTIME(분)
    private Integer        usedLeave;
    private Integer        remainingLeave;

    // JPQL constructor expression에서 사용할 "명시적" 생성자 (Number 허용!)
    public AttendanceRowView(LocalDate workDate,
                             String memberName,
                             Integer memberSeq,
                             LocalDateTime startTime,
                             LocalDateTime endTime,
                             Number overtime,            // ← BigDecimal/Long/Integer 모두 수용
                             Number usedLeave,          // ← 연차도 NUMBER면 Number로 받고 변환
                             Number remainingLeave) {
        this.workDate  = workDate;
        this.memberName = memberName;
        this.memberSeq  = memberSeq;
        this.startTime  = startTime;
        this.endTime    = endTime;
        this.overtimeMinutes  = overtime        == null ? null : overtime.intValue();
        this.usedLeave        = usedLeave      == null ? null : usedLeave.intValue();
        this.remainingLeave   = remainingLeave == null ? null : remainingLeave.intValue();
    }

    // ---- 파생 getter: JSP 그대로 사용 ----
    private static final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter HM  = DateTimeFormatter.ofPattern("HH:mm");

    public String getWorkDateStr()  { return workDate  != null ? workDate.format(YMD) : "-"; }
    public String getStartTimeStr() { return startTime != null ? startTime.toLocalTime().format(HM) : "-"; }
    public String getEndTimeStr()   { return endTime   != null ? endTime.toLocalTime().format(HM)   : "-"; }

    public String getWorkedTimeStr() {
        if (startTime == null || endTime == null) return "-";
        long mins = Duration.between(startTime, endTime).toMinutes();
        if (mins < 0) mins = 0;
        long h = mins / 60, m = mins % 60;
        return h + "h " + m + "m";
    }
    public String getOvertimeStr() {
        if (overtimeMinutes == null) return "-";
        int t = Math.max(overtimeMinutes, 0);
        return (t/60) + "h " + (t%60) + "m";
    }
    public String getUsedLeaveStr()    { return usedLeave      != null ? String.valueOf(usedLeave)      : "-"; }
    public String getRemainLeaveStr()  { return remainingLeave != null ? String.valueOf(remainingLeave) : "-"; }
}
