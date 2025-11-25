package com.example.dminfo.dao;

import com.example.dminfo.model.Conquista;
import com.example.dminfo.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ConquistaDAO implements IDAO<Conquista> {

//    CREATE TABLE IF NOT EXISTS conquista (
//            id_conquista SERIAL PRIMARY KEY,
//            descricao VARCHAR(50) NULL
//    );

    private Conquista buildConquista(ResultSet rs) throws SQLException {
        Conquista c = new Conquista();
        c.setId(rs.getInt("id_conquista"));
        c.setDescricao(rs.getString("descricao"));
        return c;
    }

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
                return buildConquista(rs);
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
    public List<Conquista> readAll(String filtro, Conexao conexao) {
        List<Conquista> conquistas = new ArrayList<>();

        String sql = "SELECT * FROM conquista";

        if (filtro != null && !filtro.isBlank()) {
            sql += " WHERE descricao LIKE '%" + filtro.replace("'", "''") + "%'";
        }

        sql += " ORDER BY id_conquista";

        ResultSet rs = conexao.consultar(sql);
        try {
            while (rs != null && rs.next()) {
                conquistas.add(buildConquista(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar conquistas: " + e.getMessage());
        }
        return conquistas;
    }

    public Conquista getById(int id, Conexao conexao) {
        String sql = "SELECT * FROM conquista WHERE id_conquista = " + id;
        ResultSet rs = conexao.consultar(sql);
        try {
            if (rs != null && rs.next()) {
                return buildConquista(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar conquista por ID: " + e.getMessage());
        }
        return null;
    }

}
