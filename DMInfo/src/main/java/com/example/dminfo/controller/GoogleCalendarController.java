package com.example.dminfo.controller;

import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.example.dminfo.util.SingletonDB;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
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
    private CriarRealizacaoAtividades atividadesModel;

    public String gerarUrlAutorizacao() {
        return new GoogleAuthorizationCodeRequestUrl(
                clientId,
                redirectUri,
                SCOPES
        ).setAccessType("offline").build();
    }

    public String trocarCodePorToken(String code) throws IOException {
        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                HTTP_TRANSPORT,
                JSON_FACTORY,
                clientId,
                clientSecret,
                code,
                redirectUri
        ).execute();

        return tokenResponse.getAccessToken();
    }

    public String sincronizarCalendario(String token) {
        try {
            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(HTTP_TRANSPORT)
                    .setJsonFactory(JSON_FACTORY)
                    .setClientSecrets(clientId, clientSecret)
                    .build()
                    .setAccessToken(token);

            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName("DM Info App")
                    .build();

            List<CriarRealizacaoAtividades> todasAtividades = atividadesModel.listarTodas();

            if (todasAtividades == null || todasAtividades.isEmpty())
                return "Nenhuma atividade encontrada no banco para sincronizar.";

            int contadorSucesso = 0;
            String calendarId = "primary";

            for (CriarRealizacaoAtividades atividade : todasAtividades) {

                Event googleEvent = converterParaGoogleEvent(atividade);
                try {
                    service.events().insert(calendarId, googleEvent).execute();
                    contadorSucesso++;
                } catch (IOException e) {
                    System.err.println("Falha ao inserir evento ID " + atividade.getId() + ": " + e.getMessage());
                }
            }

            return contadorSucesso + " atividades sincronizadas com sucesso!";

        } catch (Exception e) {
            e.printStackTrace();
            return "Erro crítico na sincronização: " + e.getMessage();
        }
    }

    private Event converterParaGoogleEvent(CriarRealizacaoAtividades atividadeApp) {
        Event event = new Event()
                .setSummary(atividadeApp.getAtv() != null ? atividadeApp.getAtv().getDescricao() : "Atividade DM Info")
                .setLocation(atividadeApp.getLocal())
                .setDescription(atividadeApp.getObservacoes());

        LocalDate dataInicio = atividadeApp.getDtIni();
        LocalDate dataFim = (atividadeApp.getDtFim() != null) ? atividadeApp.getDtFim() : dataInicio;
        Time horarioInicio = atividadeApp.getHorario();

        EventDateTime start = new EventDateTime();
        EventDateTime end = new EventDateTime();
        ZoneId zoneId = ZoneId.of(TIMEZONE_AMERICA_SAO_PAULO);

        if (horarioInicio == null) {
            String startDateStr = dataInicio.format(DateTimeFormatter.ISO_LOCAL_DATE);
            String endDateStr = dataFim.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);

            start.setDate(new com.google.api.client.util.DateTime(startDateStr));
            end.setDate(new com.google.api.client.util.DateTime(endDateStr));
        } else {
            LocalDateTime startDt = LocalDateTime.of(dataInicio, horarioInicio.toLocalTime());

            LocalTime horaFim = horarioInicio.toLocalTime().plusHours(1);
            LocalDateTime endDt = LocalDateTime.of(dataFim, horaFim);

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