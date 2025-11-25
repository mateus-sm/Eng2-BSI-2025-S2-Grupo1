package com.example.dminfo.model;

import java.time.LocalDate;

public class Administrador {

    private int id;
    private LocalDate dtIni;
    private LocalDate dtFim;
    private Usuario usuario;

    public Administrador() {}

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public LocalDate getDtIni() {return dtIni;}

    public void setDtIni(LocalDate dtIni) {this.dtIni = dtIni;}

    public LocalDate getDtFim() {return dtFim;}

    public void setDtFim(LocalDate dtFim) {this.dtFim = dtFim;}

    public Usuario getUsuario() {return usuario;}

    public void setUsuario(Usuario usuario) {this.usuario = usuario;}
}
