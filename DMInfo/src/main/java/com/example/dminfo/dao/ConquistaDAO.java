package com.example.dminfo.dao;

import com.example.dminfo.model.Conquista;
import com.example.dminfo.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ConquistaDAO implements IDAO<Conquista> {
    @Override
    public Conquista create(Conquista conquista, Conexao conexao) {
        if (conquista == null) {
            return null;
        }

        String sql = String.format(
                "INSERT INTO conquista (descricao) VALUES ('%s') RETURNING id_conquista",
                conquista.getDescricao()
        );

        ResultSet rs = conexao.consultar(sql);
        try {
            if (rs != null && rs.next()) {
                conquista.setId(rs.getInt("id_conquista"));
                return conquista;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gravar conquista: " + e.getMessage());
        }
        return null;
    }


    @Override
    public Conquista read(Conquista c, Conexao conexao) {
        String descricao = c.getDescricao();
        String sql = String.format("SELECT * FROM conquista WHERE descricao = '%s'", descricao);

        ResultSet rs = conexao.consultar(sql);

        try {
            if (rs != null && rs.next()) {
                c.setId(rs.getInt("id_conquista"));
                c.setDescricao(rs.getString("descricao"));
                return c;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar conquista: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Conquista update(Conquista conquista, Conexao conexao) {
        if (conquista != null) {
            String sql = String.format(
                    "UPDATE conquista SET descricao = '%s' WHERE id_conquista = %d",
                    conquista.getDescricao(),
                    conquista.getId()
            );

            conexao.manipular(sql);
            return conquista;
        }
        return null;
    }

    @Override
    public boolean delete(int id, Conexao conexao) {
        String sql = "DELETE FROM conquista WHERE id_conquista = " + id;
        return conexao.manipular(sql);
    }

    @Override
    public ResultSet readAll(String filtro, Conexao conexao) {
        String sql = "SELECT * FROM conquista";

        if (filtro != null && !filtro.isBlank()) {
            sql += " WHERE descricao LIKE '%" + filtro.replace("'", "''") + "%'";
        }

        sql += " ORDER BY id_conquista";

        return conexao.consultar(sql);
    }

    @Override
    public ResultSet getById(int id, Conexao conexao) {
        String sql = "SELECT * FROM conquista WHERE id_conquista = " + id;
        return conexao.consultar(sql);
    }

}
