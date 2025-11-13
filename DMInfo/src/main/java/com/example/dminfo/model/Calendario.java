package com.example.dminfo.model;

import org.springframework.stereotype.Repository;

@Repository
public class Calendario {
    private int id_calendario;
    private CriarRealizacaoAtividades id_criacao;

    public Calendario() {
    }

    public Calendario(CriarRealizacaoAtividades id_criacao) {
        this.id_criacao = id_criacao;
    }

    public int getId_calendario() {return id_calendario;}
    public void setId_calendario(int id_calendario) {this.id_calendario = id_calendario;}
    public CriarRealizacaoAtividades getId_criacao() {return id_criacao;}
    public void setId_criacao(CriarRealizacaoAtividades id_criacao) {
        this.id_criacao = id_criacao;
    }
}