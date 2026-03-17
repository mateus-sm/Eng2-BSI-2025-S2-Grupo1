package com.example.dminfo.dao;

import com.example.dminfo.model.Recurso;
import com.example.dminfo.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class RecursoDAO implements IDAO<Recurso> {

//    CREATE TABLE IF NOT EXISTS recurso (
//        id_recurso SERIAL PRIMARY KEY,
//        id_doacao INT NULL,
//        descricao VARCHAR(50) NULL,
//        tipo VARCHAR(40) NULL,
//        quantidade INT NULL,
//        CONSTRAINT fk_recurso_doacao1
//            FOREIGN KEY (id_doacao)
//            REFERENCES doacao (id_doacao)
//            ON DELETE NO ACTION
//            ON UPDATE NO ACTION
//    );

    @Override
    public ResultSet readAll(String filtro, Conexao conexao) {
        String sql = "SELECT * FROM recurso ORDER BY id_recurso";
        return conexao.consultar(sql);
    }

    @Override
    public ResultSet getById(int id, Conexao conexao) {
        String sql = "SELECT * FROM recurso WHERE id_recurso = " + id;
        return conexao.consultar(sql);
    }

    @Override
    public Recurso create(Recurso recurso, Conexao conexao) {
        if (recurso == null)
            return null;

        String sql = String.format(
                "INSERT INTO recurso (id_doacao, descricao, tipo, quantidade) VALUES (%s, '%s', '%s', %d) RETURNING id_recurso",
                recurso.getId(),
                recurso.getDescricao(),
                recurso.getTipo(),
                recurso.getQuantidade()
        );

        ResultSet rs = conexao.consultar(sql);

        try {
            if (rs != null && rs.next()) {
                return recurso;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gravar recurso: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Recurso update(Recurso recurso, Conexao conexao) {
        if (recurso != null) {
            String sql = String.format(
                    "UPDATE recurso SET id_doacao = %d, descricao = '%s', tipo = '%s', quantidade = %d WHERE id_recurso = %d",
                    recurso.getId_doacao(),
                    recurso.getDescricao(),
                    recurso.getTipo(),
                    recurso.getQuantidade(),
                    recurso.getId()
            );

            conexao.manipular(sql);
            return recurso;
        }

        return null;
    }

    @Override
    public boolean delete(int id, Conexao conexao) {
        String sql = "DELETE FROM recurso WHERE id_recurso = " + id;
        return conexao.manipular(sql);
    }

    @Override
    public Recurso read(Recurso recurso, Conexao conexao) {
        String sql = String.format("SELECT * FROM recurso WHERE id_recurso = '%d'", recurso.getId());
        ResultSet rs = conexao.consultar(sql);

        try {
            if (rs != null && rs.next()) {
                recurso.setId(rs.getInt("id_recurso"));
                recurso.setId_doacao(rs.getInt("id_doacao"));
                recurso.setDescricao(rs.getString("descricao"));
                recurso.setTipo(rs.getString("tipo"));
                recurso.setQuantidade(rs.getInt("quantidade"));
                return recurso;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar recurso: " + e.getMessage());
        }

        return null;
    }
}