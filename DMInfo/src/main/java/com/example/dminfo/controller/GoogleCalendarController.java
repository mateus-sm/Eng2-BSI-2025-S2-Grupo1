package com.example.dminfo.controller;

import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class GoogleCalendarController {

    private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TIMEZONE_AMERICA_SAO_PAULO = "America/Sao_Paulo";

    private static final List<String> SCOPES = Collections.singletonList(
            "https://www.googleapis.com/auth/calendar.events"
    );

    @Value("${google.calendar.client-id}")
    private String clientId;

    @Value("${google.calendar.client-secret}")
    private String clientSecret;

    @Value("${google.calendar.redirect-uri}")
    private String redirectUri;

    @Autowired
    private CalendarioController calendarioController;

    @GetMapping("/oauth/start")
    public RedirectView startOAuthFlow() {
        String authorizationUrl = new GoogleAuthorizationCodeRequestUrl(
                clientId,
                redirectUri,
                SCOPES
        ).setAccessType("offline").build();

        return new RedirectView(authorizationUrl);
    }

    @GetMapping("/oauth/callback")
    public RedirectView handleOAuthCallback(@RequestParam("code") String code, HttpServletResponse response) {
        try {
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    HTTP_TRANSPORT,
                    JSON_FACTORY,
                    clientId,
                    clientSecret,
                    code,
                    redirectUri
            ).execute();

            String accessToken = tokenResponse.getAccessToken();

            Cookie cookie = new Cookie("google-calendar-token", accessToken);
            cookie.setPath("/");
            cookie.setMaxAge(3600); // 1 hora de validade
            response.addCookie(cookie);

            return new RedirectView("/app/calendario?status=auth_success");
        } catch (TokenResponseException e) {
            System.err.println("Erro na troca do token: " + e.getMessage());
            return new RedirectView("/app/calendario?error=auth_failed");
        } catch (IOException e) {
            System.err.println("Erro de IO ao obter token: " + e.getMessage());
            return new RedirectView("/app/calendario?error=io_failed");
        }
    }

    @PostMapping("/sync")
    public String syncCalendar(@CookieValue(value = "google-calendar-token", required = false) String token) {
        if (token == null || token.isBlank())
            return "Erro: A autorização do Google não foi concluída. Tente novamente.";

        // Cria a credencial usando o token de acesso
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setAccessToken(token);

        // Constrói o serviço da Agenda Google
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("DM Info App")
                .build();

        List<CriarRealizacaoAtividades> atividadesParaSincronizar;
        try {
            // Usa o seu controller local para buscar as atividades
            atividadesParaSincronizar = calendarioController.listarTodasAtividades();
        } catch (Exception e) {
            return "Erro ao buscar atividades locais: " + e.getMessage();
        }

        if (atividadesParaSincronizar == null || atividadesParaSincronizar.isEmpty())
            return "Nenhuma atividade local para sincronizar.";

        String calendarId = "primary";
        int contadorSucesso = 0;

        for (CriarRealizacaoAtividades atividade : atividadesParaSincronizar) {
            Event googleEvent = converterParaGoogleEvent(atividade);
            try {
                // Insere o evento na Agenda principal do usuário
                service.events().insert(calendarId, googleEvent).execute();
                contadorSucesso++;
            } catch (IOException e) {
                System.err.println("Falha ao inserir evento: " + atividade.getAtv().getDescricao() + " - Erro: " + e.getMessage());
            }
        }

        return contadorSucesso + " de " + atividadesParaSincronizar.size() + " atividades sincronizadas com sucesso.";
    }

    private Event converterParaGoogleEvent(CriarRealizacaoAtividades atividadeApp) {
        Event event = new Event()
                .setSummary(atividadeApp.getAtv() != null ? atividadeApp.getAtv().getDescricao() : "Atividade")
                .setLocation(atividadeApp.getLocal())
                .setDescription(atividadeApp.getObservacoes());

        LocalDate dataInicio = atividadeApp.getDtIni();
        Time horarioInicio = atividadeApp.getHorario();
        LocalDate dataFim = (atividadeApp.getDtFim() != null) ? atividadeApp.getDtFim() : dataInicio;

        EventDateTime start = new EventDateTime();
        EventDateTime end = new EventDateTime();
        ZoneId zoneId = ZoneId.of(TIMEZONE_AMERICA_SAO_PAULO);

        if (horarioInicio == null) {
            // Evento de dia inteiro
            String startDateStr = dataInicio.format(DateTimeFormatter.ISO_LOCAL_DATE);
            String endDateStr = dataFim.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);

            start.setDate(new com.google.api.client.util.DateTime(startDateStr));
            end.setDate(new com.google.api.client.util.DateTime(endDateStr));
        }
        else {
            LocalDateTime startDt = LocalDateTime.of(dataInicio, horarioInicio.toLocalTime());

            LocalTime horaFim = horarioInicio.toLocalTime().plusHours(1);
            LocalDateTime endDt = LocalDateTime.of(dataFim, horaFim);

            // Converte para o formato RFC3339
            String startRfc3339 = startDt.atZone(zoneId).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            String endRfc3339 = endDt.atZone(zoneId).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            start.setDateTime(new com.google.api.client.util.DateTime(startRfc3339));
            start.setTimeZone(TIMEZONE_AMERICA_SAO_PAULO);

            end.setDateTime(new com.google.api.client.util.DateTime(endRfc3339));
            end.setTimeZone(TIMEZONE_AMERICA_SAO_PAULO);
        }

        event.setStart(start);
        event.setEnd(end);

        return event;
    }
}