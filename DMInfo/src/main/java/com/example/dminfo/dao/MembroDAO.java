package com.example.dminfo.dao;

import com.example.dminfo.model.Membro;
import com.example.dminfo.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MembroDAO {

    public List<Membro> get(String termoBusca, Conexao conexao) {
        List<Membro> membros = new ArrayList<>();
        String filtroSQL = "";

        if (termoBusca != null && !termoBusca.trim().isEmpty())
            filtroSQL = " WHERE UPPER(u.nome) LIKE UPPER('%" + termoBusca.trim() + "%')";

        String sql = """
            SELECT 
                m.id_membro, m.dtini, m.dtfim, m.observacao, m.id_usuario, u.nome
            FROM membro m 
            LEFT JOIN usuario u ON m.id_usuario = u.id_usuario
        """ + filtroSQL + " ORDER BY u.nome";

        try {
            ResultSet rs = conexao.consultar(sql);
            if (rs != null)
                while (rs.next())
                    membros.add(new Membro(rs));
        } catch (SQLException e) {
            System.out.println("Erro ao listar Membros: " + e.getMessage());
        }
        return membros;
    }

    public Membro get(int id, Conexao conexao) {
        String sql = """
            SELECT 
                m.id_membro, m.dtini, m.dtfim, m.observacao, m.id_usuario, u.nome
            FROM membro m 
            LEFT JOIN usuario u ON m.id_usuario = u.id_usuario
            WHERE m.id_membro = """ + id;

        try {
            ResultSet rs = conexao.consultar(sql);
            if (rs != null && rs.next())
                return new Membro(rs);
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Membro por ID: " + e.getMessage());
        }
        return null;
    }

    public Membro getByUsuario(int idUsuario, Conexao conexao) {
        String sql = """
            SELECT 
                m.id_membro, m.dtini, m.dtfim, m.observacao, m.id_usuario, u.nome
            FROM membro m 
            LEFT JOIN usuario u ON m.id_usuario = u.id_usuario
            WHERE m.id_usuario = """ + idUsuario;

        try {
            ResultSet rs = conexao.consultar(sql);
            if (rs != null && rs.next())
                return new Membro(rs);
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Membro por Usuário: " + e.getMessage());
        }
        return null;
    }

    public Membro gravar(Membro membro, Conexao conexao) {
        String dtFimVal = (membro.getDtFim() == null) ? "NULL" : "'" + membro.getDtFim().toString() + "'";

        String sql = String.format(
                "INSERT INTO membro (dtini, dtfim, observacao, id_usuario) VALUES ('%s', %s, '%s', %d) RETURNING id_membro",
                membro.getDtIni().toString(),
                dtFimVal,
                membro.getObservacao(),
                membro.getUsuario().getId()
        );

        try {
            ResultSet rs = conexao.consultar(sql);
            if (rs != null && rs.next()) {
                membro.setId(rs.getInt("id_membro"));
                return membro;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gravar Membro: " + e.getMessage());
        }
        return null;
    }

    public boolean alterar(Membro membro, Conexao conexao) {
        String dtFimVal = (membro.getDtFim() == null) ? "NULL" : "'" + membro.getDtFim().toString() + "'";

        String sql = String.format(
                "UPDATE membro SET observacao = '%s', dtfim = %s WHERE id_membro = %d",
                membro.getObservacao(),
                dtFimVal,
                membro.getId()
        );
        return conexao.manipular(sql);
    }

    public boolean excluir(int id, Conexao conexao) {
        String sql = "DELETE FROM membro WHERE id_membro = " + id;
        return conexao.manipular(sql);
    }
}