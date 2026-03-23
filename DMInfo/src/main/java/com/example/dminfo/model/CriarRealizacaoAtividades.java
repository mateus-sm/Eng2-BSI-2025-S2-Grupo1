package com.example.dminfo.model;

import com.example.dminfo.dao.CriarRealizacaoAtividadesDAO;
import com.example.dminfo.util.Conexao;
import com.example.dminfo.model.state.EstadoAtividade;
import com.example.dminfo.model.state.EstadoAtividadeAtiva;
import com.example.dminfo.model.observer.Observer;
import com.example.dminfo.model.observer.Sujeito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CriarRealizacaoAtividades implements Sujeito {
    private int id;
    private Administrador admin;
    private Atividade atv;
    private Time horario;
    private String local;
    private String observacoes;
    private LocalDate dtIni;
    private LocalDate dtFim;
    private double custoprevisto;
    private double custoreal;
    private Boolean status;

    private transient List<Observer> observadores = new ArrayList<>();

    @Autowired
    private CriarRealizacaoAtividadesDAO dao;

    public CriarRealizacaoAtividades() {}

    public CriarRealizacaoAtividades(int id, Administrador admin, Atividade atv, Time horario, String local, String observacoes, LocalDate dtIni, LocalDate dtFim, double custoprevisto, double custoreal, boolean status) {
        this.id = id;
        this.admin = admin;
        this.atv = atv;
        this.horario = horario;
        this.local = local;
        this.observacoes = observacoes;
        this.dtIni = dtIni;
        this.dtFim = dtFim;
        this.custoprevisto = custoprevisto;
        this.custoreal = custoreal;
        this.status = status;
    }

    private CriarRealizacaoAtividades montarAtividade(ResultSet rs) throws SQLException {
        Administrador admin = new Administrador();
        admin.setId(rs.getInt("id_admin"));

        try {
            Usuario usuarioAdmin = new Usuario();
            usuarioAdmin.setLogin(rs.getString("admin_usuario"));
            admin.setUsuario(usuarioAdmin);
        } catch (SQLException ignored) {

        }

        Atividade atividade = new Atividade();
        atividade.setId(rs.getInt("id_atividade"));
        try {
            atividade.setDescricao(rs.getString("atividade_descricao"));
        } catch (SQLException ignored) {

        }

        CriarRealizacaoAtividades cra = new CriarRealizacaoAtividades();
        cra.setId(rs.getInt("id_criacao"));
        cra.setAdmin(admin);
        cra.setAtv(atividade);
        cra.setHorario(rs.getTime("horario"));
        cra.setLocal(rs.getString("local"));
        cra.setObservacoes(rs.getString("observacoes"));

        if (rs.getDate("dtini") != null) {
            cra.setDtIni(rs.getDate("dtini").toLocalDate());
        }
        if (rs.getDate("dtfim") != null) {
            cra.setDtFim(rs.getDate("dtfim").toLocalDate());
        }

        cra.setCustoprevisto(rs.getDouble("custoprevisto"));
        cra.setCustoreal(rs.getDouble("custoreal"));
        cra.setStatus(rs.getBoolean("status"));

        return cra;
    }

    public List<CriarRealizacaoAtividades> listarTodas(Conexao conexao) {
        List<CriarRealizacaoAtividades> lista = new ArrayList<>();
        ResultSet rs = dao.listarTodas(conexao);
        try {
            if (rs != null) {
                while (rs.next()) {
                    lista.add(montarAtividade(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar atividades: " + e.getMessage());
        }
        return lista;
    }

    public CriarRealizacaoAtividades buscarPorId(Integer id, Conexao conexao) {
        ResultSet rs = dao.getById(id, conexao);
        try {
            if (rs != null && rs.next()) {
                return montarAtividade(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar atividade: " + e.getMessage());
        }
        return null;
    }

    // Aplicação do STATE corrigida (Delegação e Cadeia)
    public boolean finalizar(CriarRealizacaoAtividades atividadeAtualizada, Conexao conexao) {
        if (atividadeAtualizada.getId() <= 0) {
            throw new RuntimeException("ID inválido.");
        }

        CriarRealizacaoAtividades atividadeNoBanco = this.buscarPorId(atividadeAtualizada.getId(), conexao);

        if (atividadeNoBanco == null) {
            throw new RuntimeException("Atividade não encontrada na base de dados.");
        }

        // SEMPRE chama a classe inicial, não importa qual seja o status no banco
        EstadoAtividade estadoInicial = new EstadoAtividadeAtiva();

        // O estadoInicial fará o "if" internamente. Se não for ele, ele chama a outra.
        boolean sucesso = estadoInicial.finalizar(atividadeAtualizada, atividadeNoBanco, dao, conexao);

        // Notifica o Calendário e o E-mail se a ação no banco deu certo
        if (sucesso == true) {
            atividadeAtualizada.notificar();
        }

        return sucesso;
    }

    @Override
    public void add(Observer observer) {
        if (!observadores.contains(observer)) {
            observadores.add(observer);
        }
    }

    @Override
    public void remover(Observer observer) {
        observadores.remove(observer);
    }

    @Override
    public void notificar() {
        for (Observer observer : observadores) {
            observer.update(this);
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Administrador getAdmin() { return admin; }
    public void setAdmin(Administrador admin) { this.admin = admin; }
    public Atividade getAtv() { return atv; }
    public void setAtv(Atividade atv) { this.atv = atv; }
    public Time getHorario() { return horario; }
    public void setHorario(Time horario) { this.horario = horario; }
    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public LocalDate getDtIni() { return dtIni; }
    public void setDtIni(LocalDate dtIni) { this.dtIni = dtIni; }
    public LocalDate getDtFim() { return dtFim; }
    public void setDtFim(LocalDate dtFim) { this.dtFim = dtFim; }
    public double getCustoprevisto() { return custoprevisto; }
    public void setCustoprevisto(double custoprevisto) { this.custoprevisto = custoprevisto; }
    public double getCustoreal() { return custoreal; }
    public void setCustoreal(double custoreal) { this.custoreal = custoreal; }
    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }
}