package com.example.dminfo.model;

import com.example.dminfo.dao.EventoDAO;
import com.example.dminfo.util.Conexao;
import com.example.dminfo.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class Evento {

    private int id;
    private Administrador admin;
    private String titulo;
    private String descricao;

    @Autowired(required = false)
    private EventoDAO dao;

    @Autowired
    private Administrador administradorModel; // Dependência trazida do DAO

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

    private Evento montarEvento(ResultSet rs) throws SQLException {
        int adminId = rs.getInt("id_admin");
        Administrador admin = administradorModel.getById(adminId, SingletonDB.getConexao());

        // se nao tiver admin com o id, usa o mock
        if (admin == null) {
            Administrador adminMock = new Administrador();
            adminMock.setId(adminId);
            admin = adminMock;
        }

        return new Evento(
                rs.getInt("id_evento"),
                admin,
                rs.getString("titulo"),
                rs.getString("descricao")
        );
    }

    public List<Evento> listar(String termoDescricao, String ordenarPor) {
        List<Evento> eventos = new ArrayList<>();
        ResultSet rs = dao.buscarEventos(termoDescricao, ordenarPor);
        try {
            if (rs != null) {
                while (rs.next()) {
                    eventos.add(montarEvento(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar Eventos com filtros: " + e.getMessage());
        }
        return eventos;
    }

    public List<Evento> getTodos(Conexao conexao) {
        List<Evento> eventos = new ArrayList<>();
        ResultSet rs = dao.getTodos();
        try {
            if (rs != null) {
                while (rs.next()) {
                    eventos.add(montarEvento(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar todos os Eventos: " + e.getMessage());
        }
        return eventos;
    }

    public Evento getById(Integer id) {
        ResultSet rs = dao.getById(id);
        try {
            if (rs != null && rs.next()) {
                return montarEvento(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar evento por ID: " + e.getMessage());
        }
        return null;
    }

    public Evento salvar(Evento evento) {
        if (evento.getTitulo() == null || evento.getTitulo().trim().isEmpty())
            throw new RuntimeException("O título do evento é obrigatório.");

        return dao.gravar(evento);
    }

    public Evento atualizar(Integer id, Evento evento) {
        Evento existente = this.getById(id);
        if (existente == null)
            throw new RuntimeException("Evento com ID " + id + " não encontrado para atualização.");

        existente.setTitulo(evento.getTitulo());
        existente.setDescricao(evento.getDescricao());

        if (dao.alterar(existente))
            return existente;
        throw new RuntimeException("Falha ao atualizar o evento no banco de dados.");
    }

    public boolean excluir(Integer id) {
        Evento existente = this.getById(id);
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