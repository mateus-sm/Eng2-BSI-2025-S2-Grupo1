package com.example.dminfo.model;

import com.example.dminfo.dao.ConquistaDAO;
import com.example.dminfo.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class Conquista {
    private int id;
    private String descricao;

    @Autowired
    private ConquistaDAO dao;

    public Conquista() {}

    public Conquista(String descricao) {
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    //Métodos
    private Conquista buildConquista(ResultSet rs) throws SQLException {
        Conquista c = new Conquista();
        c.setId(rs.getInt("id_conquista"));
        c.setDescricao(rs.getString("descricao"));
        return c;
    }

    public List<Conquista> listar(String filtro, Conexao conexao) {
        List<Conquista> conquistas = new ArrayList<>();
        ResultSet rs = dao.readAll(filtro, conexao);

        try {
            while (rs != null && rs.next()) {
                conquistas.add(buildConquista(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar conquistas: " + e.getMessage());
        }

        return conquistas;
    }

    public Conquista getById(Integer id, Conexao conexao) {
        ResultSet rs = dao.getById(id, conexao);

        try {
            if (rs != null && rs.next()) {
                return buildConquista(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro construir Conquista na Model: " + e.getMessage());
        }

        return null;
    }

    public Conquista getByDesc(Conquista conquista, Conexao conexao) {
        return dao.read(conquista, conexao);
    }

    public Conquista salvar(Conquista conquista, Conexao conexao) {
        if (conquista == null || conquista.getDescricao() == null || conquista.getDescricao().isBlank()) {
            throw new RuntimeException("Descrição da conquista é obrigatória.");
        }

        Conquista existente = dao.read(conquista, conexao);
        if (existente != null) {
            throw new RuntimeException("Já existe uma conquista com essa descrição.");
        }

        return dao.create(conquista, conexao);
    }

    public Conquista update(Integer id, Conquista conquistaDetails, Conexao conexao) {
        Conquista conquistaBanco = getById(id, conexao);

        if (conquistaBanco == null) {
            throw new RuntimeException("Conquista não encontrada para o ID: " + id);
        }

        conquistaBanco.setDescricao(conquistaDetails.getDescricao());

        Conquista c = dao.update(conquistaBanco, conexao);
        if (c != null) {
            return conquistaBanco;
        }
        throw new RuntimeException("Erro ao atualizar conquista.");
    }

    public boolean excluir(Integer id, Conexao conexao) {
        Conquista conquista = getById(id, conexao);

        if (conquista == null) {
            throw new RuntimeException("Conquista não encontrada.");
        }

        return dao.delete(id, conexao);
    }
}
