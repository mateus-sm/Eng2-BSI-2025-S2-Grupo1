package com.example.dminfo.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "administrador")
public class Administrador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id_admin")
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "dtini")
    private LocalDate dtIni;

    @Column(name = "dtfim")
    private LocalDate dtFim;

    public Administrador() {

    }

    public Administrador(int idadmin, Usuario user, LocalDate dtinicio, LocalDate dtfinal) {
        this.id = idadmin;
        this.usuario = user;
        this.dtIni = dtinicio;
        this.dtFim = dtfinal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
}
