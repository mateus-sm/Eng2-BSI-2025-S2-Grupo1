package com.example.dminfo.controller;

import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.example.dminfo.util.Conexao;
import com.example.dminfo.util.SingletonDB;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/apis/notificacao")
@Service
public class NotificacaoController {

    private Conexao conexao;
    private final JavaMailSender mailSender;

    private static final DateTimeFormatter DATE_FORMATTER_BR =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("pt", "BR"));

    private static class MembroNotificacao {
        String nome;
        String email;

        public MembroNotificacao(String nome, String email) {
            this.nome = nome;
            this.email = email;
        }
    }

    @Autowired
    public NotificacaoController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private List<MembroNotificacao> buscarMembrosComEmail(int idCriacao) {
        this.conexao = SingletonDB.getConexao();

        String sql = String.format(
                "SELECT u.nome, u.email " +
                        "FROM criar_realizacao_atividades_membro cram " +
                        "JOIN membro m ON cram.membro_id_membro = m.id_membro " +
                        "JOIN usuario u ON m.id_usuario = u.id_usuario " +
                        "WHERE cram.criar_realizacao_atividades_id_criacao = %d", idCriacao);

        ResultSet rs = null;
        List<MembroNotificacao> membros = new ArrayList<>();

        try {
            if (this.conexao == null) {
                System.err.println("ERRO CRÍTICO: Conexão com o banco de dados é NULL.");
                return membros;
            }
            rs = this.conexao.consultar(sql);
            while (rs != null && rs.next()) {
                membros.add(new MembroNotificacao(rs.getString("nome"), rs.getString("email")));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar membros para notificação: " + e.getMessage());
        }
        return membros;
    }

    private void enviarNotificacao(CriarRealizacaoAtividades atividade, List<MembroNotificacao> membros, String motivo) {
        if (membros.isEmpty()) {
            System.out.println("Não há membros para notificar na atividade: " + atividade.getAtv().getDescricao());
            return;
        }

        String[] destinatarios = membros.stream()
                .map(m -> m.email)
                .toArray(String[]::new);

        String dataFormatada = atividade.getDtIni().format(DATE_FORMATTER_BR);

        String tituloAtividade = atividade.getAtv().getDescricao();
        String assunto = String.format("Lembrete: Sua participação na atividade '%s' (%s)", tituloAtividade, motivo);

        String corpoBase = String.format(
                "Prezado(s) membro(s),\n\n" +
                        "Esta é uma notificação sobre a sua participação na seguinte atividade:\n\n" +
                        "• Título: %s\n" +
                        "• Data: %s\n" +
                        "• Horário: %s\n" +
                        "• Local: %s\n\n" +
                        "Motivo do Lembrete: Não se esqueça!\n\n",
                tituloAtividade,
                dataFormatada,
                atividade.getHorario() != null ? atividade.getHorario().toString() : "Não especificado",
                atividade.getLocal());

        String corpoFinal = corpoBase +
                "Atenciosamente,\n" +
                "Sistema de Gerenciamento de Atividades.";

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(destinatarios);
            message.setSubject(assunto);
            message.setText(corpoFinal);

            this.mailSender.send(message);

            System.out.printf("SUCESSO: E-mail enviado para %d membros da atividade '%s'.\n", membros.size(), tituloAtividade);
        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO ao enviar e-mail: " + e.getMessage());
            throw new RuntimeException("Falha ao enviar e-mail através do JavaMailSender.", e);
        }
    }

    private CriarRealizacaoAtividades buscarAtividadePorId(int idCriacao) {
        this.conexao = SingletonDB.getConexao();

        String sql = String.format(
                "SELECT cra.*, atv.descricao AS atividade_descricao " +
                        "FROM criar_realizacao_atividades cra " +
                        "JOIN atividade atv ON cra.id_atividade = atv.id_atividade " +
                        "WHERE cra.id_criacao = %d", idCriacao);

        ResultSet rs = null;
        try {
            if (this.conexao == null) {
                System.err.println("ERRO CRÍTICO: Conexão com o banco de dados é NULL.");
                return null;
            }
            rs = this.conexao.consultar(sql);
            if (rs != null && rs.next()) {
                CriarRealizacaoAtividades cra = new CriarRealizacaoAtividades();
                cra.setId(rs.getInt("id_criacao"));
                cra.setDtIni(rs.getDate("dtini").toLocalDate());
                cra.setHorario(rs.getTime("horario"));
                cra.setLocal(rs.getString("local"));

                com.example.dminfo.model.Atividade atv = new com.example.dminfo.model.Atividade();
                atv.setDescricao(rs.getString("atividade_descricao"));
                cra.setAtv(atv);

                return cra;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar atividade por ID: " + e.getMessage());
        }
        return null;
    }

    @PostMapping("/manual/{idCriacao}")
    public ResponseEntity<String> enviarNotificacaoManual(@PathVariable int idCriacao) {
        CriarRealizacaoAtividades atividade = buscarAtividadePorId(idCriacao);

        if (atividade == null || atividade.getAtv() == null) {
            return ResponseEntity.badRequest().body("Erro: Atividade não encontrada ou incompleta.");
        }

        List<MembroNotificacao> membros = buscarMembrosComEmail(idCriacao);

        if (membros.isEmpty()) {
            return ResponseEntity.ok("Notificação não enviada: Nenhum membro associado à atividade.");
        }

        try {
            enviarNotificacao(atividade, membros, "\uD83D\uDDD3\uFE0F");
            return ResponseEntity.ok(String.format("Notificação manual enviada com sucesso para %d membros da atividade '%s'.",
                    membros.size(), atividade.getAtv().getDescricao()));
        } catch (Exception e) {
            String errorMessage = e.getMessage().contains("JavaMailSender") ? e.getMessage() : "Erro interno ao tentar enviar a notificação. Verifique as credenciais SMTP.";
            System.err.println("Erro ao enviar notificação manual: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorMessage);
        }
    }

    @Scheduled(cron = "0 0 10 * * *")
    public void agendarNotificacoes() {
        System.out.println("Iniciando verificação de notificações agendadas...");
        notificarAtividadesEm(7, "7 Dias Antes");
        notificarAtividadesEm(1, "24 Horas Antes");
        System.out.println("Verificação de notificações concluída.");
    }

    private void notificarAtividadesEm(int diasAntes, String motivo) {
        this.conexao = SingletonDB.getConexao();

        LocalDate dataLimite = LocalDate.now().plusDays(diasAntes);
        String dataSql = dataLimite.format(DateTimeFormatter.ISO_DATE);

        String sql = String.format(
                "SELECT cra.*, atv.descricao AS atividade_descricao " +
                        "FROM criar_realizacao_atividades cra " +
                        "JOIN atividade atv ON cra.id_atividade = atv.id_atividade " +
                        "JOIN calendario c ON cra.id_criacao = c.id_criacao " +
                        "WHERE cra.dtini = '%s'", dataSql);

        ResultSet rs = null;
        List<CriarRealizacaoAtividades> atividadesParaNotificar = new ArrayList<>();

        try {
            if (this.conexao == null) {
                System.err.println("ERRO CRÍTICO: Conexão com o banco de dados é NULL. Não foi possível buscar atividades.");
                return;
            }
            rs = this.conexao.consultar(sql);
            while (rs != null && rs.next()) {
                CriarRealizacaoAtividades cra = new CriarRealizacaoAtividades();
                cra.setId(rs.getInt("id_criacao"));
                cra.setDtIni(rs.getDate("dtini").toLocalDate());
                cra.setHorario(rs.getTime("horario"));
                cra.setLocal(rs.getString("local"));

                com.example.dminfo.model.Atividade atv = new com.example.dminfo.model.Atividade();
                atv.setDescricao(rs.getString("atividade_descricao"));
                cra.setAtv(atv);

                atividadesParaNotificar.add(cra);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar atividades para notificação agendada: " + e.getMessage());
        }

        for (CriarRealizacaoAtividades atividade : atividadesParaNotificar) {
            List<MembroNotificacao> membros = buscarMembrosComEmail(atividade.getId());
            enviarNotificacao(atividade, membros, motivo);
        }
    }
}