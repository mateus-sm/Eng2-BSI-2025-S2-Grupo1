package com.example.dminfo.model;

import jakarta.persistence.*;

import java.sql.Time;
import java.time.LocalDate;

@Entity
@Table(name = "criar_realizacao_atividades")
public class CriarRealizacaoAtividades{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id_criacao")
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin")
    private Administrador admin;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_atividade")
    private Atividade atv;

    @Column(name = "horario")
    private Time horario;

    @Column(name = "local")
    private String local;

    @Column(name = "observacoes")
    private String observacoes;

    @Column(name = "dtini")
    private LocalDate dtIni;

    @Column(name = "dtfim")
    private LocalDate dtFim;

    @Column(name = "custoprevisto")
    private double custoprevisto;

    @Column(name = "custoreal")
    private double custoreal;

    public CriarRealizacaoAtividades(){

    }

    public CriarRealizacaoAtividades(int idcr, Administrador adm, Atividade ativ, Time h, String local, String obs, LocalDate dtIni, LocalDate dtFim, double custop, double custor){
        this.id = idcr;
        this.admin = adm;
        this.atv = ativ;
        this.horario = h;
        this.local = local;
        this.observacoes = obs;
        this.dtIni = dtIni;
        this.dtFim = dtFim;
        this.custoprevisto = custop;
        this.custoreal = custor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Administrador getAdmin() {
        return admin;
    }

    public void setAdmin(Administrador admin) {
        this.admin = admin;
    }

    public Atividade getAtv() {
        return atv;
    }

    public void setAtv(Atividade atv) {
        this.atv = atv;
    }

    public Time getHorario() {
        return horario;
    }

    public void setHorario(Time horario) {
        this.horario = horario;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDate getDtIni() {
        return dtIni;
    }

    public void setDtIni(LocalDate dtIni) {
        this.dtIni = dtIni;
    }

    public LocalDate getDtFim() {
        return dtFim;
    }

    public void setDtFim(LocalDate dtFim) {
        this.dtFim = dtFim;
    }

    public double getCustoprevisto() {
        return custoprevisto;
    }

    public void setCustoprevisto(double custoprevisto) {
        this.custoprevisto = custoprevisto;
    }

    public double getCustoreal() {
        return custoreal;
    }

    public void setCustoreal(double custoreal) {
        this.custoreal = custoreal;
    }
}
