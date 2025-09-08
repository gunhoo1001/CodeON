package com.spring.app.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;

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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.spring.app.domain.AttendanceRowView;  // ✅ DTO
import com.spring.app.entity.Department;
import com.spring.app.entity.Grade;
import com.spring.app.model.AttendanceRepository;
import com.spring.app.model.DepartmentRepository;
import com.spring.app.model.GradeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HrAttendanceService_imple implements HrAttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final DepartmentRepository departmentRepository;
    private final GradeRepository gradeRepository;

    @Override
    public Page<AttendanceRowView> getAttendancePage(YearMonth month, Long deptSeq, Long gradeSeq, Pageable pageable) {
        LocalDate from = month.atDay(1);
        LocalDate to   = month.atEndOfMonth();
        return attendanceRepository.findPageForMonth(from, to, deptSeq, gradeSeq, pageable); // ✅ Long 그대로
    }

    @Override
    public List<Department> getDepartments() { return departmentRepository.findAll(); }

    @Override
    public List<Grade> getGrades() { return gradeRepository.findAll(); }

	@Override
	public void attendance_to_Excel(YearMonth month, Long deptSeq, Integer gradeSeq, Model model) {
		LocalDate from = month.atDay(1);
        LocalDate to   = month.atEndOfMonth();

        // 엑셀은 전체 조회(비페이징)
        List<AttendanceRowView> rows = attendanceRepository.findAllForMonth(from, to, deptSeq, gradeSeq);

        SXSSFWorkbook workbook = new SXSSFWorkbook();
        SXSSFSheet sheet = workbook.createSheet("근태-" + month);

        // 열 너비
        int[] columnWidths = {4000, 4000, 3600, 3600, 3600, 4200, 3000, 3000, 3600};
        for (int i = 0; i < columnWidths.length; i++) {
            sheet.setColumnWidth(i, columnWidths[i]);
        }

        int rowLocation = 0;

        // 스타일: 머지 타이틀
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

        // 헤더 스타일
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderTop(BorderStyle.THICK);
        headerStyle.setBorderBottom(BorderStyle.THICK);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // 바디 스타일(센터)
        CellStyle center = workbook.createCellStyle();
        center.setAlignment(HorizontalAlignment.CENTER);

        // 날짜 스타일(근무일용)
        CellStyle dateCellStyle = workbook.createCellStyle();
        CreationHelper helper = workbook.getCreationHelper();
        dateCellStyle.setDataFormat(helper.createDataFormat().getFormat("yyyy-MM-dd"));
        dateCellStyle.setAlignment(HorizontalAlignment.CENTER);

        // ===== 타이틀(머지) =====
        Row mergeRow = sheet.createRow(rowLocation);
        for (int i = 0; i < 9; i++) {
            Cell cell = mergeRow.createCell(i);
            cell.setCellStyle(mergeRowStyle);
            cell.setCellValue("CodeON 전사 근태 (" + month + ")");
        }
        sheet.addMergedRegion(new CellRangeAddress(rowLocation, rowLocation, 0, 8));

        // ===== 헤더 =====
        Row headerRow = sheet.createRow(++rowLocation);
        String[] headers = {"근무일", "사원명", "사번", "출근시각", "퇴근시각", "근무시간", "사용연차", "잔여연차", "연장근무"};
        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers[i]);
            headerCell.setCellStyle(headerStyle);
        }

        // ===== 데이터 =====
        for (int i = 0; i < rows.size(); i++) {
            AttendanceRowView r = rows.get(i);
            Row body = sheet.createRow(++rowLocation);

            int c = 0;
            // 근무일(날짜 셀)
            Cell c0 = body.createCell(c++);
            if (r.getWorkDate() != null) {
                c0.setCellValue(Date.valueOf(r.getWorkDate()));
                c0.setCellStyle(dateCellStyle);
            } else {
                c0.setCellValue("-");
                c0.setCellStyle(center);
            }

            body.createCell(c).setCellValue(nz(r.getMemberName())); body.getCell(c++).setCellStyle(center);
            body.createCell(c).setCellValue(r.getMemberSeq() == null ? "" : String.valueOf(r.getMemberSeq())); body.getCell(c++).setCellStyle(center);
            body.createCell(c).setCellValue(nz(r.getStartTimeStr())); body.getCell(c++).setCellStyle(center);
            body.createCell(c).setCellValue(nz(r.getEndTimeStr()));   body.getCell(c++).setCellStyle(center);
            body.createCell(c).setCellValue(nz(r.getWorkedTimeStr()));body.getCell(c++).setCellStyle(center);
            body.createCell(c).setCellValue(nz(r.getUsedLeaveStr())); body.getCell(c++).setCellStyle(center);
            body.createCell(c).setCellValue(nz(r.getRemainLeaveStr()));body.getCell(c++).setCellStyle(center);
            body.createCell(c).setCellValue(nz(r.getOvertimeStr()));  body.getCell(c++).setCellStyle(center);
        }

        // View 로 전달 (Member 스타일 동일)
        model.addAttribute("locale", Locale.KOREA);
        model.addAttribute("workbookName", "CodeON_근태_" + month);
        model.addAttribute("workbook", workbook);
    }

    private static String nz(String v) { return v == null ? "-" : v; }
}
