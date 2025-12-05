package com.example.dminfo.model;

import com.example.dminfo.dao.EventoDAO;
import com.example.dminfo.util.Conexao; // Import necessário
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    public List<Evento> listar(String termoDescricao, String ordenarPor) {
        return dao.buscarEventos(termoDescricao, ordenarPor);
    }

    public List<Evento> getTodos(Conexao conexao) {
        return dao.getTodos();
    }

    public Evento getById(Integer id) {
        return dao.getById(id);
    }

    public Evento salvar(Evento evento) {
        if (evento.getTitulo() == null || evento.getTitulo().trim().isEmpty())
            throw new RuntimeException("O título do evento é obrigatório.");

        return dao.gravar(evento);
    }

    public Evento atualizar(Integer id, Evento evento) {
        Evento existente = dao.getById(id);
        if (existente == null)
            throw new RuntimeException("Evento com ID " + id + " não encontrado para atualização.");

        existente.setTitulo(evento.getTitulo());
        existente.setDescricao(evento.getDescricao());

        if (dao.alterar(existente))
            return existente;
        throw new RuntimeException("Falha ao atualizar o evento no banco de dados.");
    }

    public boolean excluir(Integer id) {
        Evento existente = dao.getById(id);
        if (existente == null)
            return false;
        return dao.excluir(id);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Administrador getAdmin() { return admin; }
    public void setAdmin(Administrador admin) { this.admin = admin; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}