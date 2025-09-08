package com.spring.app.excel;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.web.servlet.view.AbstractView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ExcelDownloadView extends AbstractView {

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {

        // 1) locale / 파일명 기본값 보정
        Locale locale = (Locale) model.get("locale");
        if (locale == null) locale = Locale.KOREA;

        String workbookName = (String) model.get("workbookName");
        if (workbookName == null || workbookName.isBlank()) workbookName = "export";

        // 2) 시간 포맷 (24시간 권장: HH). 기존 12시간 유지하려면 "hhmmss"로 바꾸세요.
        java.util.Date now = new java.util.Date();
        SimpleDateFormat dayformat  = new SimpleDateFormat("yyyyMMdd", locale);
        SimpleDateFormat hourformat = new SimpleDateFormat("HHmmss",  locale);
        String day  = dayformat.format(now);
        String hour = hourformat.format(now);
        String fileName = workbookName + "_" + day + "_" + hour + ".xlsx";

        // 3) 브라우저별 파일명 인코딩 (UA null 방어)
        String browser = request.getHeader("User-Agent");
        if (browser == null) browser = "";

        if (browser.contains("MSIE") || browser.contains("Trident")) {           // IE / IE11
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } else if (browser.contains("Firefox") || browser.contains("Opera") || browser.contains("Safari")) {
            fileName = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1") + "\"";
        } else if (browser.contains("Chrome")) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < fileName.length(); i++) {
                char c = fileName.charAt(i);
                if (c > '~') sb.append(URLEncoder.encode(String.valueOf(c), "UTF-8"));
                else sb.append(c);
            }
            fileName = sb.toString();
        } else {
            fileName = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1") + "\"";
        }

        // 4) 응답 헤더 (표준 xlsx MIME)
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ";");
        response.setHeader("Content-Transfer-Encoding", "binary");

        // 5) Workbook 안전 캐스팅 (SXSSF/XSSF/HSSF 모두 지원)
        Workbook workbook = (Workbook) model.get("workbook");
        if (workbook == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "workbook is null");
            return;
        }

        // 6) 스트리밍 & 자원 정리
        try (OutputStream ost = response.getOutputStream()) {
            workbook.write(ost);
            ost.flush();
        } finally {
            try { workbook.close(); } catch (Exception ignore) {}
            if (workbook instanceof SXSSFWorkbook sx) {
                try { sx.dispose(); } catch (Exception ignore) {} // SXSSF temp 파일 정리
            }
        }
    }
}
