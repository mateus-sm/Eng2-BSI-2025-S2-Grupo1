package com.example.dminfo.dao;

import com.example.dminfo.model.Doacao;
import com.example.dminfo.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

@Repository
public class DoacaoDAO {

    public ResultSet getById(int id, Conexao conexao) {
        String sql = "SELECT d.*, don.nome AS doador_nome, a.id_usuario, u.nome AS usuario_nome " +
                "FROM doacao d " +
                "JOIN doador don ON d.id_doador = don.id_doador " +
                "JOIN administrador a ON d.id_admin = a.id_admin " +
                "JOIN usuario u ON a.id_usuario = u.id_usuario " +
                "WHERE d.id_doacao = " + id;

        return conexao.consultar(sql);
    }

    public ResultSet readAll(String filtro, Conexao conexao) {
        String whereClause = (filtro != null && !filtro.isEmpty()) ? filtro : "";

        String sql = "SELECT d.*, don.nome AS doador_nome, a.id_usuario, u.nome AS usuario_nome " +
                "FROM doacao d " +
                "JOIN doador don ON d.id_doador = don.id_doador " +
                "JOIN administrador a ON d.id_admin = a.id_admin " +
                "JOIN usuario u ON a.id_usuario = u.id_usuario " +
                whereClause + " ORDER BY d.data DESC";

        return conexao.consultar(sql);
    }

    public Doacao create(Doacao doacao, Conexao conexao) {
        if (doacao == null) return null;

        String obsEscapada = (doacao.getObservacao() != null ? doacao.getObservacao() : "").replace("'", "''");

        String sql = String.format(Locale.US,
                "INSERT INTO doacao (id_doador, id_admin, data, valor, observacao) " +
                        "VALUES (%d, %d, '%s', %f, '%s') RETURNING id_doacao",
                doacao.getId_doador().getId(),
                doacao.getId_admin().getId(),
                doacao.getData().toString(),
                doacao.getValor(),
                obsEscapada
        );

        ResultSet rs = conexao.consultar(sql);
        try {
            if (rs != null && rs.next()) {
                doacao.setId_doacao(rs.getInt("id_doacao"));
                return doacao;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gravar Doação (SQL): " + e.getMessage());
        }
        return null;
    }

    public Doacao update(Doacao doacao, Conexao conexao) {
        if (doacao == null) return null;

        String obsEscapada = (doacao.getObservacao() != null ? doacao.getObservacao() : "").replace("'", "''");

        String sql = String.format(Locale.US,
                "UPDATE doacao SET " +
                        "id_doador = %d, " +
                        "id_admin = %d, " +
                        "valor = %f, " +
                        "observacao = '%s' " +
                        "WHERE id_doacao = %d",
                doacao.getId_doador().getId(),
                doacao.getId_admin().getId(),
                doacao.getValor(),
                obsEscapada,
                doacao.getId_doacao()
        );

        conexao.consultar(sql);
        return doacao;
    }

    public boolean delete(int id, Conexao conexao) {
        String sql = String.format("DELETE FROM doacao WHERE id_doacao = %d", id);
        return conexao.manipular(sql);
    }
}