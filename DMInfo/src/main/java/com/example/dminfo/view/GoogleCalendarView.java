package com.example.dminfo.view;

import com.example.dminfo.controller.GoogleCalendarController;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/calendar")
public class GoogleCalendarView {

    @Autowired
    private GoogleCalendarController googleController;

    @GetMapping("/oauth/start")
    public RedirectView startOAuthFlow() {
        String authorizationUrl = googleController.gerarUrlAutorizacao();
        return new RedirectView(authorizationUrl);
    }

    @GetMapping("/oauth/callback")
    public RedirectView handleOAuthCallback(@RequestParam("code") String code, HttpServletResponse response) {
        try {
            String accessToken = googleController.trocarCodePorToken(code);

            Cookie cookie = new Cookie("google-calendar-token", accessToken);
            cookie.setPath("/");
            cookie.setMaxAge(3600);
            response.addCookie(cookie);

            return new RedirectView("/app/calendario?status=auth_success");
        } catch (Exception e) {
            System.err.println("Erro OAuth: " + e.getMessage());
            return new RedirectView("/app/calendario?error=auth_failed");
        }
    }

    @PostMapping("/sync")
    public String syncCalendar(@CookieValue(value = "google-calendar-token", required = false) String token) {
        if (token == null || token.isBlank())
            return "Erro: A autorização do Google não foi concluída. Tente novamente.";
        return googleController.sincronizarCalendario(token);
    }
}