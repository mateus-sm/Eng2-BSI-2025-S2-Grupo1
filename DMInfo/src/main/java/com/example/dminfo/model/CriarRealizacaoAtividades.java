package com.example.dminfo.model;

import com.example.dminfo.dao.CriarRealizacaoAtividadesDAO;
import com.example.dminfo.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

@Repository
public class CriarRealizacaoAtividades {
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

    @Autowired
    private CriarRealizacaoAtividadesDAO dao;

    public CriarRealizacaoAtividades() {}

    public CriarRealizacaoAtividades(int id, Administrador admin, Atividade atv, Time horario, String local,
                                     String observacoes, LocalDate dtIni, LocalDate dtFim, double custoprevisto, double custoreal, boolean status) {
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

    public List<CriarRealizacaoAtividades> listarTodas(Conexao conexao) {
        return dao.listarTodas(conexao);
    }

    public CriarRealizacaoAtividades buscarPorId(Integer id, Conexao conexao) {
        return dao.getById(id, conexao);
    }

    public boolean finalizar(CriarRealizacaoAtividades atividadeAtualizada, Conexao conexao) {
        if (atividadeAtualizada.getId() <= 0) {
            throw new RuntimeException("ID inválido.");
        }

        return dao.finalizarAtividade(atividadeAtualizada, conexao);
    }
}