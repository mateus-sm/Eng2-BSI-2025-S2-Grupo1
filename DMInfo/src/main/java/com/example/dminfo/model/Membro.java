package com.example.dminfo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "membro")
public class Membro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id_membro")
    private Integer id;

    @Column(name = "dtini")
    private LocalDate dtIni;

    @Column(name = "dtfim")
    private LocalDate dtFim;

    @Column(name = "observacao")
    private String observacao;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    public Membro() {
    }

    public Membro(int iddm, LocalDate dtIni, LocalDate dtFim, String observacao, Usuario usuario) {
        this.id = iddm;
        this.dtIni = dtIni;
        this.dtFim = dtFim;
        this.observacao = observacao;
        this.usuario = usuario;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}