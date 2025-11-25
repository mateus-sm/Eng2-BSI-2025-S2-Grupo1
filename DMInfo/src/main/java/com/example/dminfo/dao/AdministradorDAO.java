package com.example.dminfo.dao;

import com.example.dminfo.model.Administrador;
import com.example.dminfo.model.Usuario;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AdministradorDAO {

    private Administrador buildAdministrador(ResultSet rs) throws SQLException {
        Administrador admin = new Administrador();
        admin.setId(rs.getInt("id_admin"));

        if (rs.getDate("dtini") != null) {
            admin.setDtIni(rs.getDate("dtini").toLocalDate());
        }
        if (rs.getDate("dtfim") != null) {
            admin.setDtFim(rs.getDate("dtfim").toLocalDate());
        }

        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id_usuario"));

        admin.setUsuario(usuario);
        return admin;
    }

    public Administrador get(int id) {
        String sql = "SELECT * FROM administrador WHERE id_admin = " + id;
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                return buildAdministrador(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Administrador por ID: " + e.getMessage());
        }
        return null;
    }

    public List<Administrador> get(String filtro) {
        List<Administrador> admins = new ArrayList<>();
        String sql = "SELECT * FROM administrador " + filtro;

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null) {
                while (rs.next()) {
                    admins.add(buildAdministrador(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar Administradores: " + e.getMessage());
        }
        return admins;
    }

    public Administrador getByUsuario(int usuarioId) {
        String sql = "SELECT * FROM administrador WHERE id_usuario = " + usuarioId;
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                return buildAdministrador(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Administrador por Usuário: " + e.getMessage());
        }
        return null;
    }

    public Administrador gravar(Administrador admin) {
        String sql = String.format("INSERT INTO administrador (dtini, id_usuario) " +
                        "VALUES ('%s', %d) RETURNING id_admin",
                admin.getDtIni().toString(),
                admin.getUsuario().getId()
        );

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
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

    public boolean alterar(Administrador admin) {
        String sql = String.format("UPDATE administrador SET dtfim = '%s' WHERE id_admin = %d",
                admin.getDtFim().toString(),
                admin.getId()
        );
        return SingletonDB.getConexao().manipular(sql);
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM administrador WHERE id_admin = " + id;
        return SingletonDB.getConexao().manipular(sql);
    }
}