package com.example.dminfo.controller;

import com.example.dminfo.model.Calendario;
import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.example.dminfo.util.Conexao;
import com.example.dminfo.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/apis/notificacao")
public class NotificacaoController {

    private final JavaMailSender mailSender;

    @Autowired
    private CriarRealizacaoAtividades atividadeModel;

    @Autowired
    public NotificacaoController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    //ROTA MANUAL

    @PostMapping("/manual/{idCriacao}")
    public ResponseEntity<String> enviarNotificacaoManual(@PathVariable int idCriacao) {
        Conexao conexao = SingletonDB.getConexao();

        CriarRealizacaoAtividades atividade = atividadeModel.buscarPorId(idCriacao, conexao);

        if (atividade == null) {
            return ResponseEntity.badRequest().body("Erro: Atividade não encontrada.");
        }

        Calendario calendario = new Calendario(atividade);

        calendario.carregarEInstanciarObservadores(conexao);

        calendario.notificarObservadores("\uD83D\uDDD3\uFE0F Lembrete Manual", this.mailSender);

        return ResponseEntity.ok("Notificação manual disparada com sucesso pelo Calendário.");
    }

    //ROTINA AGENDADA (Roda todo dia às 10h)
    @Scheduled(cron = "0 0 10 * * *")
    public void agendarNotificacoes() {
        System.out.println("Iniciando verificação de notificações agendadas via Calendário...");

        //Dispara 7 dias antes
        dispararAgendamentoPorDias(7, "7 Dias Antes");

        //Dispara 1 dia antes
        dispararAgendamentoPorDias(1, "24 Horas Antes");

        System.out.println("Verificação de notificações concluída.");
    }

    private void dispararAgendamentoPorDias(int diasAntes, String motivo) {
        Conexao conexao = SingletonDB.getConexao();
        LocalDate dataLimite = LocalDate.now().plusDays(diasAntes);
        String dataSql = dataLimite.format(DateTimeFormatter.ISO_DATE);

        List<CriarRealizacaoAtividades> atividadesDoDia = atividadeModel.buscarAtividadesPorData(dataSql, conexao);

        if (atividadesDoDia.isEmpty()) {
            System.out.println("Nenhuma atividade encontrada para: " + dataSql + " (" + motivo + ")");
            return;
        }

        for (CriarRealizacaoAtividades atividade : atividadesDoDia) {
            Calendario calendario = new Calendario(atividade);
            calendario.carregarEInstanciarObservadores(conexao);
            calendario.notificarObservadores(motivo, this.mailSender);
        }
    }
}