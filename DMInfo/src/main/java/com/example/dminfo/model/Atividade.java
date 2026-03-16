package com.example.dminfo.model;

import com.example.dminfo.dao.AtividadeDAO;
import com.example.dminfo.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class Atividade {
    private int id;
    private Evento evento;
    private String descricao;

    @Autowired
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

    private Atividade montarAtividade(ResultSet rs) throws SQLException {
        Evento eventoMock = new Evento();
        eventoMock.setId(rs.getInt("id_evento"));

        return new Atividade(
                rs.getInt("id_atividade"),
                eventoMock,
                rs.getString("descricao")
        );
    }

    public Atividade getById(int id, Conexao conexao) {
        ResultSet rs = dao.get(id, conexao);
        try {
            if (rs != null && rs.next()) {
                return montarAtividade(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar atividade por ID: " + e.getMessage());
        }
        return null;
    }

    public List<Atividade> listarPorEvento(int idEvento, Conexao conexao) {
        List<Atividade> lista = new ArrayList<>();
        ResultSet rs = dao.getPorEvento(idEvento, conexao);
        try {
            if (rs != null) {
                while (rs.next()) {
                    lista.add(montarAtividade(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar Atividades por Evento: " + e.getMessage());
        }
        return lista;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}