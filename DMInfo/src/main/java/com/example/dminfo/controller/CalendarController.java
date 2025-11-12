package com.example.dminfo.controller;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final List<String> SCOPES = Collections.singletonList(
            "https://www.googleapis.com/auth/calendar.events"
    );

    @Value("${google.calendar.client-id}")
    private String clientId;

    @Value("${google.calendar.client-secret}")
    private String clientSecret;

    @Value("${google.calendar.redirect-uri}")
    private String redirectUri;

    private static GoogleTokenResponse tokenResponse;

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
    public String handleOAuthCallback(@RequestParam String code) throws IOException {

        tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                HTTP_TRANSPORT,
                JSON_FACTORY,
                clientId,
                clientSecret,
                code,
                redirectUri
        ).execute();

        return "Autorização da Agenda Google concluída com sucesso! Agora você pode sincronizar.";
    }

    @PostMapping("/sync")
    public String syncCalendar() throws IOException {
        if (tokenResponse == null) {
            return "Erro: A autorização do Google não foi concluída. Inicie o fluxo OAuth primeiro.";
        }

        try {
            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(HTTP_TRANSPORT)
                    .setJsonFactory(JSON_FACTORY)
                    .setClientSecrets(clientId, clientSecret)
                    .build()
                    .setFromTokenResponse(tokenResponse);

            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName("DMINFO-Membros-App")
                    .build();

            Event event = createMembroEventExample();
            String calendarId = "primary";

            event = service.events().insert(calendarId, event).execute();

            return "Evento de Exemplo sincronizado com sucesso na Agenda: " + event.getHtmlLink();

        } catch (TokenResponseException e) {
            return "Erro de Token: O token de acesso expirou. Por favor, reinicie a autorização.";
        } catch (IOException e) {
            return "Erro de Sincronização: " + e.getMessage();
        }
    }

    private Event createMembroEventExample() {
        Event event = new Event()
                .setSummary("Sincronização de Membro (Exemplo)")
                .setDescription("Evento de teste criado para a funcionalidade de sincronização.");

        com.google.api.client.util.DateTime startDateTime =
                new com.google.api.client.util.DateTime("2026-01-01T10:00:00-03:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/Sao_Paulo");
        event.setStart(start);

        com.google.api.client.util.DateTime endDateTime =
                new com.google.api.client.util.DateTime("2026-01-01T11:00:00-03:00");
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/Sao_Paulo");
        event.setEnd(end);

        return event;
    }
}