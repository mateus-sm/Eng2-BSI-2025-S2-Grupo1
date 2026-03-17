package com.example.dminfo.model;

import com.example.dminfo.dao.RecursoDAO;
import com.example.dminfo.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class Recurso {
    private int id;
    private int id_doacao;
    private String descricao;
    private String tipo;
    private int quantidade;

    @Autowired
    private RecursoDAO dao;

    public Recurso() { }

    public Recurso(String descricao, int id, int id_doacao, int quantidade, String tipo) {
        this.descricao = descricao;
        this.id = id;
        this.id_doacao = id_doacao;
        this.quantidade = quantidade;
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_doacao() {
        return id_doacao;
    }

    public void setId_doacao(int id_doacao) {
        this.id_doacao = id_doacao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    // Métodos
    private Recurso buildRecurso(ResultSet rs) throws SQLException {
        Recurso r = new Recurso();
        r.setId(rs.getInt("id_recurso"));
        r.setId_doacao(rs.getInt("id_doacao"));
        r.setDescricao(rs.getString("descricao"));
        r.setTipo(rs.getString("tipo"));
        r.setQuantidade(rs.getInt("quantidade"));
        return r;
    }

    public List<Recurso> listar(String filtro, Conexao conexao) {
        List<Recurso> recursos = new ArrayList<>();
        ResultSet rs = dao.readAll(filtro, conexao);

        try {
            while (rs != null && rs.next()) {
                recursos.add(buildRecurso(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar recursos: " + e.getMessage());
        }

        return recursos;
    }

    public Recurso consultar(Recurso recurso, Conexao conexao) {
        return dao.read(recurso, conexao);
    }

    public Recurso getById(int id, Conexao conexao) {
        ResultSet rs = dao.getById(id, conexao);

        try {
            if (rs != null && rs.next()) {
                return buildRecurso(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar recurso por ID: " + e.getMessage());
        }

        return null;
    }

    public Recurso alterar(Recurso recurso, Conexao conexao) {
        return dao.update(recurso, conexao);
    }

    public Recurso gravar(Recurso recurso, Conexao conexao) {
        if (recurso == null)
            return null;

        return dao.create(recurso, conexao);
    }

    public boolean excluir(int id, Conexao conexao) {
        return dao.delete(id, conexao);
    }
}
