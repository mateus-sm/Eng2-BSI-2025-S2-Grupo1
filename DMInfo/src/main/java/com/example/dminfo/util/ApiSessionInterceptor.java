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

        jakarta.servlet.http.HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("idUsuarioLogado") == null) {
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }
}
