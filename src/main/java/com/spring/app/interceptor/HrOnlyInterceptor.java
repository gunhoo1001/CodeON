package com.spring.app.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.spring.app.domain.MemberDTO;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class HrOnlyInterceptor implements HandlerInterceptor {

    private static final int HR_DEPT_SEQ = 10; // 인사팀 dept_seq

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        HttpSession session = req.getSession(false);
        if (session == null) return deny(req, res);

        MemberDTO login = (MemberDTO) session.getAttribute("loginuser");
        if (login == null) return deny(req, res);

        if (login.getFkDepartmentSeq() == HR_DEPT_SEQ) {
            return true; // 인사팀이면 통과
        }

        return deny(req, res);
    }

    private boolean deny(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String referer = req.getHeader("Referer");
        if (referer == null || referer.isBlank()) {
            referer = req.getContextPath() + "/";
        }
        req.setAttribute("message", "권한이 없습니다. (인사팀 전용 메뉴)");
        req.setAttribute("loc", referer);

        RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/views/msg.jsp");
        rd.forward(req, res);
        return false;
    }
}
