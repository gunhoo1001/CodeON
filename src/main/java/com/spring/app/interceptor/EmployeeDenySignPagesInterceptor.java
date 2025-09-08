package com.spring.app.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.spring.app.domain.MemberDTO;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class EmployeeDenySignPagesInterceptor implements HandlerInterceptor {

    private static final int GRADE_EMPLOYEE = 1; // 사원

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        HttpSession session = req.getSession(false);
        if (session == null) return false;

        MemberDTO login = (MemberDTO) session.getAttribute("loginuser");
        if (login == null) return false;

        boolean isEmployee = (login.getFkGradeSeq() == GRADE_EMPLOYEE);
        if (!isEmployee) return true; // 사원이 아니면 통과

        // ── 사원: 접근 차단 ─────────────────────────────────────
        // AJAX/Fetch면 403 JSON
        String xhr = req.getHeader("X-Requested-With");
        String accept = req.getHeader("Accept");
        boolean wantsJson = "XMLHttpRequest".equalsIgnoreCase(xhr)
                         || (accept != null && accept.contains("application/json"));
        if (wantsJson) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write("{\"ok\":false,\"reason\":\"forbidden\"}");
            return false;
        }

        // 일반 요청: 이전 페이지로 돌려보내기 (Referer 사용, 없으면 홈으로)
        String referer = req.getHeader("Referer");
        // referer가 없거나, 현재 URL과 같으면(무한루프 방지) 홈으로
        String current = req.getRequestURL().toString();
        if (referer == null || referer.isBlank() || referer.equals(current)) {
            referer = req.getContextPath() + "/";
        }

        req.setAttribute("message", "권한이 없습니다. (사원은 해당 메뉴 이용 불가)");
        req.setAttribute("loc", referer);
        RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/views/msg.jsp");
        rd.forward(req, res);
        return false;
    }
}
