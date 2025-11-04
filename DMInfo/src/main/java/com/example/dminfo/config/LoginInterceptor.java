package com.example.dminfo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession(false);

        boolean usuarioEstaLogado = (session != null && session.getAttribute("usuarioLogado") != null);

        if (usuarioEstaLogado) {
            return true;
        } else {
            response.sendRedirect("/app/login");
            return false;
        }
    }
}