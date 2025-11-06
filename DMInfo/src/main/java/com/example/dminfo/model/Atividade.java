package com.example.dminfo.model;

import com.example.dminfo.dao.AtividadeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class Atividade {
    private int id;
    private Evento evento;
    private String descricao;

    @Autowired(required = false)
    private AtividadeDAO dao;

    public Atividade() {}

    public Atividade(int id, Evento evento, String descricao) {
        this.id = id;
        this.evento = evento;
        this.descricao = descricao;
    }

    public Atividade(Evento evento, String descricao) {
        this.evento = evento;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}