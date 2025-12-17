package com.example.dminfo.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiSessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);
        String uri = request.getRequestURI();

        if (session == null || session.getAttribute("idUsuarioLogado") == null) {
            response.sendRedirect("/login");
            return false;
        }

        if (uri.startsWith("/apis/administrador")) {
            Boolean isAdm = (Boolean) session.getAttribute("isAdm");
            if (isAdm == null || !isAdm) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        }

        return true;
    }
}