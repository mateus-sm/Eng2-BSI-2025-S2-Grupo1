package com.example.dminfo.dao;

import com.example.dminfo.model.Administrador;
import com.example.dminfo.util.Conexao;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class AdministradorDAO {


    public ResultSet get(Conexao conexao) {
        String sql = """
            SELECT 
                a.id_admin,
                a.dtini,
                a.dtfim,
                a.id_usuario,
                u.nome AS usuario_nome
            FROM administrador a
            JOIN usuario u ON u.id_usuario = a.id_usuario
            ORDER BY a.id_admin;
            """;

        return conexao.consultar(sql);
    }

    public ResultSet get(int id, Conexao conexao) {
        String sql = String.format("""
            SELECT 
                a.id_admin,
                a.dtini,
                a.dtfim,
                a.id_usuario,
                u.nome AS usuario_nome
            FROM administrador a
            JOIN usuario u ON u.id_usuario = a.id_usuario
            WHERE a.id_admin = %d
            """, id);

        return conexao.consultar(sql);
    }

    public ResultSet getByUsuario(int usuarioId, Conexao conexao) {
        String sql = String.format("""
            SELECT 
                a.id_admin,
                a.dtini,
                a.dtfim,
                a.id_usuario,
                u.nome AS usuario_nome
            FROM administrador a
            JOIN usuario u ON u.id_usuario = a.id_usuario
            WHERE a.id_usuario = %d
            """, usuarioId);

        return conexao.consultar(sql);
    }

    public ResultSet filtrar(String nome, String dtIni, String dtFim, Conexao conexao) {
        String sql = """
            SELECT 
                a.id_admin,
                a.dtini,
                a.dtfim,
                a.id_usuario,
                u.nome AS usuario_nome
            FROM administrador a
            JOIN usuario u ON a.id_usuario = u.id_usuario
            WHERE 1=1
        """;

        if (nome != null && !nome.trim().isEmpty()) {
            sql += " AND u.nome ILIKE '%" + nome + "%'";
        }

        if (dtIni != null && !dtIni.trim().isEmpty()) {
            sql += " AND a.dtini >= '" + dtIni + "'";
        }

        if (dtFim != null && !dtFim.trim().isEmpty()) {
            sql += " AND a.dtini <= '" + dtFim + "'";
        }

        sql += " ORDER BY u.nome";

        return conexao.consultar(sql);
    }

    public int contar(Conexao conexao) {
        String sql = "SELECT COUNT(*) AS total FROM administrador";
        int total = 0;
        try {
            ResultSet rs = conexao.consultar(sql);
            if (rs != null && rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao contar administradores: " + e.getMessage());
        }
        return total;
    }

    public Administrador gravar(Administrador admin, Conexao conexao) {
        String sql = String.format("INSERT INTO administrador (dtini, id_usuario) VALUES ('%s', %d) RETURNING id_admin",
                admin.getDtIni().toString(), admin.getUsuario().getId());

        ResultSet rs = conexao.consultar(sql);
        try {
            if (rs != null && rs.next()) {
                admin.setId(rs.getInt("id_admin"));
                return admin;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gravar Administrador: " + e.getMessage());
        }
        return null;
    }

    public boolean alterar(Administrador admin, Conexao conexao) {
        String dtFimSql = (admin.getDtFim() != null) ? ("'" + admin.getDtFim().toString() + "'") : "NULL";
        String sql = String.format("UPDATE administrador SET dtfim = %s WHERE id_admin = %d", dtFimSql, admin.getId());
        return conexao.manipular(sql);
    }

    public boolean excluir(int id, Conexao conexao) {
        String sql = "DELETE FROM administrador WHERE id_admin = " + id;
        return conexao.manipular(sql);
    }
}