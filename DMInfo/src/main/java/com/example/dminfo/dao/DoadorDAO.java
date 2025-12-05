package com.example.dminfo.dao;

import com.example.dminfo.model.Doador;
import com.example.dminfo.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class DoadorDAO {

    public Doador create(Doador doador, Conexao conexao) {
        if (doador == null) return null;

        String sql = String.format("INSERT INTO doador (nome, documento, rua, bairro, cidade, uf, cep, email, telefone, contato) " +
                        "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s') RETURNING id_doador",
                doador.getNome().replace("'", "''"),
                doador.getDocumento().replace("'", "''"),
                doador.getRua().replace("'", "''"),
                doador.getBairro().replace("'", "''"),
                doador.getCidade().replace("'", "''"),
                doador.getUf().replace("'", "''"),
                doador.getCep().replace("'", "''"),
                doador.getEmail().replace("'", "''"),
                doador.getTelefone().replace("'", "''"),
                doador.getContato().replace("'", "''")
        );

        ResultSet rs = conexao.consultar(sql);
        try {
            if (rs != null && rs.next()) {
                doador.setId(rs.getInt("id_doador"));
                return doador;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gravar Doador: " + e.getMessage());
        }
        return null;
    }

    public Doador update(Doador doador, Conexao conexao) {
        if (doador != null) {
            String sql = String.format("UPDATE doador SET nome = '%s', documento = '%s', rua = '%s', bairro = '%s', " +
                            "cidade = '%s', uf = '%s', cep = '%s', email = '%s', telefone = '%s', contato = '%s' " +
                            "WHERE id_doador = %d",
                    doador.getNome().replace("'", "''"),
                    doador.getDocumento().replace("'", "''"),
                    doador.getRua().replace("'", "''"),
                    doador.getBairro().replace("'", "''"),
                    doador.getCidade().replace("'", "''"),
                    doador.getUf().replace("'", "''"),
                    doador.getCep().replace("'", "''"),
                    doador.getEmail().replace("'", "''"),
                    doador.getTelefone().replace("'", "''"),
                    doador.getContato().replace("'", "''"),
                    doador.getId()
            );

            conexao.manipular(sql);
            return doador;
        }
        return null;
    }

    public boolean delete(int id, Conexao conexao) {
        String sql = "DELETE FROM doador WHERE id_doador = " + id;
        return conexao.manipular(sql);
    }

    public ResultSet read(Doador d, Conexao conexao) {
        String documento = d.getDocumento().replace("'", "''");
        String sql = String.format("SELECT * FROM doador WHERE documento = '%s'", documento);
        return conexao.consultar(sql);
    }

    public ResultSet readAll(String filtro, Conexao conexao) {
        String sql = "SELECT * FROM doador";

        if (filtro != null && !filtro.isBlank()) {
            sql += " WHERE nome LIKE '%" + filtro.replace("'", "''") + "%'";
        }

        sql += " ORDER BY id_doador";
        return conexao.consultar(sql);
    }

    public ResultSet getById(int id, Conexao conexao) {
        String sql = "SELECT * FROM doador WHERE id_doador = " + id;
        return conexao.consultar(sql);
    }
}