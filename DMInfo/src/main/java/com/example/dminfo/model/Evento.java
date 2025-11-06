package com.example.dminfo.model;

import com.example.dminfo.dao.EventoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class Evento {

    private int id;
    private Administrador admin;
    private String titulo;
    private String descricao;


    @Autowired(required = false)
    private EventoDAO dao;

    public Evento() {}

    public Evento(int id, Administrador admin, String titulo, String descricao) {
        this.id = id;
        this.admin = admin;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public Evento(Administrador admin, String titulo, String descricao) {
        this.admin = admin;
        this.titulo = titulo;
        this.descricao = descricao;
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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}