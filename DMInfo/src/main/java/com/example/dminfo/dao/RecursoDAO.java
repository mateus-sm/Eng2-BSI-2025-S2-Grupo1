package com.example.dminfo.dao;

import com.example.dminfo.model.Recurso; // Assumindo que o model Recurso existe
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RecursoDAO {

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

    private Recurso buildRecurso(ResultSet rs) throws SQLException {
        Recurso r = new Recurso();
        r.setId(rs.getInt("id_recurso"));

        int idDoacao = rs.getInt("id_doacao");
        if (!rs.wasNull()) {
            r.setId(idDoacao);
        }

        r.setDescricao(rs.getString("descricao"));
        r.setTipo(rs.getString("tipo"));
        r.setQuantidade(rs.getInt("quantidade"));

        return r;
    }

    public List<Recurso> listar() {
        List<Recurso> recursos = new ArrayList<>();
        String sql = "SELECT * FROM recurso ORDER BY id_recurso";
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            while (rs != null && rs.next()) {
                recursos.add(buildRecurso(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar recursos: " + e.getMessage());
        }
        return recursos;
    }

    public Recurso getById(int id) {
        String sql = "SELECT * FROM recurso WHERE id_recurso = " + id;
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                return buildRecurso(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar recurso por ID: " + e.getMessage());
        }
        return null;
    }

    public Recurso gravar(Recurso recurso) {
        if (recurso == null)
            return null;

        String sql = String.format(
                "INSERT INTO recurso (id_doacao, descricao, tipo, quantidade) VALUES (%s, '%s', '%s', %d) RETURNING id_recurso",
                recurso.getId(),
                recurso.getDescricao(),
                recurso.getTipo(),
                recurso.getQuantidade()
        );

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                recurso.setId(rs.getInt("id_recurso"));
                return recurso;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gravar recurso: " + e.getMessage());
        }
        return null;
    }


    public boolean alterar(Recurso recurso) {
        if (recurso != null) {

            String sql = String.format(
                    "UPDATE recurso SET id_doacao = %s, descricao = '%s', tipo = '%s', quantidade = %d WHERE id_recurso = %d",
                    recurso.getId(),
                    recurso.getDescricao(),
                    recurso.getTipo(),
                    recurso.getQuantidade(),
                    recurso.getId()
            );

            return SingletonDB.getConexao().manipular(sql);
        }
        return false;
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM recurso WHERE id_recurso = " + id;
        return SingletonDB.getConexao().manipular(sql);
    }

    public Recurso consultar(String descricao) {
        String sql = String.format("SELECT * FROM recurso WHERE descricao = '%s'", descricao);
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                return buildRecurso(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar recurso: " + e.getMessage());
        }
        return null;
    }
}