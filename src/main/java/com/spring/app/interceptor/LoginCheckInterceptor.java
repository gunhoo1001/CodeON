package com.spring.app.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 세션을 새로 만들지 않음
        HttpSession session = request.getSession(false);
        Object login = (session != null ? session.getAttribute("loginuser") : null);
        if (login != null) return true; // 로그인 되어 있으면 통과

        // ── 로그인 안 된 상태: msg.jsp로 포워딩 ─────────────────────
        // 원래 가려던 주소 (로그인 성공 후 복귀용)
        String uri = request.getRequestURI();
        String qs  = request.getQueryString();
        String dest = uri + (qs != null ? "?" + qs : "");

        String ctx = request.getContextPath();
        String loginPage = ctx + "/login/loginStart";
        String loc = loginPage + "?redirect=" +
                     java.net.URLEncoder.encode(dest, java.nio.charset.StandardCharsets.UTF_8);

        request.setAttribute("message", "먼저 로그인 하세요.");
        request.setAttribute("loc",     loc);  // msg.jsp에서 확인 후 바로 로그인 페이지로 이동

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/msg.jsp");
        dispatcher.forward(request, response);
        return false; // 컨트롤러로 진행 막기
    }
}
