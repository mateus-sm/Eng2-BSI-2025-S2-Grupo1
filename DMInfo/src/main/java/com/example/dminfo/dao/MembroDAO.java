package com.example.dminfo.dao;

import com.example.dminfo.model.Membro;
import com.example.dminfo.model.Usuario;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MembroDAO {

    private Membro buildMembro(ResultSet rs) throws SQLException {
        Membro membro = new Membro();
        membro.setId(rs.getInt("id_membro"));

        if (rs.getDate("dtini") != null)
            membro.setDtIni(rs.getDate("dtini").toLocalDate());

        if (rs.getDate("dtfim") != null)
            membro.setDtFim(rs.getDate("dtfim").toLocalDate());

        membro.setObservacao(rs.getString("observacao"));

        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id_usuario"));
        usuario.setNome(rs.getString("nome"));

        membro.setUsuario(usuario);
        return membro;
    }

    public Membro gravar(Membro membro) {
        String sql = String.format("INSERT INTO membro (dtini, dtfim, observacao, id_usuario) " +
                        "VALUES ('%s', %s, '%s', %d) RETURNING id_membro",
                membro.getDtIni().toString(),
                membro.getDtFim() == null ? "NULL" : "'" + membro.getDtFim().toString() + "'",
                membro.getObservacao(),
                membro.getUsuario().getId()
        );

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                membro.setId(rs.getInt("id_membro"));
                return membro;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gravar Membro: " + e.getMessage());
        }
        return null;
    }

    public boolean alterar(Membro membro) {
        String sql = String.format("UPDATE membro SET observacao = '%s', dtfim = %s " +
                        "WHERE id_membro = %d",
                membro.getObservacao(),
                membro.getDtFim() == null ? "NULL" : "'" + membro.getDtFim().toString() + "'",
                membro.getId()
        );
        return SingletonDB.getConexao().manipular(sql);
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM membro WHERE id_membro = " + id;
        return SingletonDB.getConexao().manipular(sql);
    }

    public List<Membro> get(String termoBusca) {
        List<Membro> membros = new ArrayList<>();
        String filtroSQL = "";

        if (termoBusca != null && !termoBusca.trim().isEmpty())
            filtroSQL = " WHERE UPPER(u.nome) LIKE UPPER('%" + termoBusca.trim() + "%')";

        String sql = "SELECT " +
                "    m.id_membro AS id_membro, m.dtini AS dtini, " +
                "    m.dtfim AS dtfim, m.observacao AS observacao, m.id_usuario AS id_usuario, " +
                "    u.nome AS nome " +
                "FROM membro m LEFT JOIN usuario u ON m.id_usuario = u.id_usuario " + filtroSQL;

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null) {
                while (rs.next()) {
                    membros.add(buildMembro(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("### ERRO AO LISTAR MEMBROS (DAO) ###");
            e.printStackTrace();
        }
        return membros;
    }

    public Membro get(int id) {
        String sql = "SELECT " +
                "    m.id_membro AS id_membro, m.dtini AS dtini, " +
                "    m.dtfim AS dtfim, m.observacao AS observacao, m.id_usuario AS id_usuario, " +
                "    u.nome AS nome " +
                "FROM membro m LEFT JOIN usuario u ON m.id_usuario = u.id_usuario " +
                "WHERE m.id_membro = " + id;

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                return buildMembro(rs);
            }
        } catch (SQLException e) {
            System.err.println("### ERRO AO BUSCAR MEMBRO POR ID (DAO) ###");
            e.printStackTrace();
        }
        return null;
    }

    public boolean existsByUsuarioId(int usuarioId) {
        String sql = "SELECT 1 FROM membro WHERE id_usuario = " + usuarioId;
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar existência de Membro por Usuário: " + e.getMessage());
        }
        return false;
    }
}