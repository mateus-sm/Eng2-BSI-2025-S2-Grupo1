package com.example.dminfo.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
public class ApiSessionInterceptor implements HandlerInterceptor {

    // LISTA NEGRA: Rotas que APENAS Administradores podem acessar
    // Adicione aqui todas as telas ou APIs críticas
    private static final List<String> ROTAS_RESTRITAS = Arrays.asList(
            "/app/administradores",       // Tela de gestão de admins
            "/apis/administrador",        // API de admins
            "/app/parametrizacao",        // Configurações do sistema
            "/app/financeiro"             // Exemplo hipotético
    );

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

        for (String rota : ROTAS_RESTRITAS) {
            if (uri.startsWith(rota)) {

                Boolean isAdm = (Boolean) session.getAttribute("isAdm");

                if (isAdm == null || !isAdm) {
                    System.out.println("ACESSO NEGADO: Usuário " + session.getAttribute("idUsuarioLogado") + " tentou acessar " + uri);

                    if (uri.startsWith("/apis/")) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("{\"erro\": \"Acesso negado. Requer privilégios de administrador.\"}");
                    } else {
                        response.sendRedirect("/app/principal?erro=sem_permissao");
                    }
                    return false;
                }
            }
        }

        return true;
    }
}