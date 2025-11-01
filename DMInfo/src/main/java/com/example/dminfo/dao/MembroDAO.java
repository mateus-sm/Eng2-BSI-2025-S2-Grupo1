package com.example.dminfo.dao;

import com.example.dminfo.model.Membro;
import com.example.dminfo.model.Usuario;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MembroDAO {

    // Metodo para construir o objeto Membro a partir do ResultSet
    private Membro buildMembro(ResultSet rs) throws SQLException {
        Membro membro = new Membro();
        membro.setId(rs.getInt("id_membro"));
        membro.setCodigo(rs.getInt("codigo_membro"));

        // Trata datas que podem ser nulas
        if (rs.getDate("dtini") != null) {
            membro.setDtIni(rs.getDate("dtini").toLocalDate());
        }
        if (rs.getDate("dtfim") != null) {
            membro.setDtFim(rs.getDate("dtfim").toLocalDate());
        }

        membro.setObservacao(rs.getString("observacao"));

        // Cria e anexa o objeto Usuario
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id_usuario"));

        // Se sua consulta (JOIN) também buscar o nome do usuário, adicione aqui
        // Ex: usuario.setNome(rs.getString("usr_nome"));

        membro.setUsuario(usuario);
        return membro;
    }

    public Membro gravar(Membro membro) {
        String sql = String.format("INSERT INTO membro (codigo_membro, dtini, dtfim, observacao, id_usuario) " +
                        "VALUES (%d, '%s', %s, '%s', %d) RETURNING id_membro",
                membro.getCodigo(),
                membro.getDtIni().toString(),
                membro.getDtFim() == null ? "NULL" : "'" + membro.getDtFim().toString() + "'",
                membro.getObservacao(),
                membro.getUsuario().getId()
        );

        ResultSet rs = SingletonDB.getConexao().consultar(sql); // 'consultar' para pegar o RETURNING
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
        String sql = String.format("UPDATE membro SET codigo_membro = %d, observacao = '%s', dtfim = %s " +
                        "WHERE id_membro = %d",
                membro.getCodigo(),
                membro.getObservacao(),
                membro.getDtFim() == null ? "NULL" : "'" + membro.getDtFim().toString() + "'",
                membro.getId()
        );
        return SingletonDB.getConexao().executar(sql); // 'executar' para UPDATE
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM membro WHERE id_membro = " + id;
        return SingletonDB.getConexao().executar(sql);
    }

    public List<Membro> get(String filtro) {
        List<Membro> membros = new ArrayList<>();
        String sql = "SELECT * FROM membro " + filtro; // O filtro pode estar vazio

        // IMPORTANTE: Você precisa fazer um JOIN se quiser o nome do usuário
        // Ex: "SELECT m.*, u.usr_nome FROM membro m JOIN usuario u ON m.id_usuario = u.id_usuario " + filtro;

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null) {
                while (rs.next()) {
                    membros.add(buildMembro(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar Membros: " + e.getMessage());
        }
        return membros;
    }

    public Membro get(int id) {
        String sql = "SELECT * FROM membro WHERE id_membro = " + id;
        // Ex: "SELECT m.*, u.usr_nome FROM membro m JOIN usuario u ON m.id_usuario = u.id_usuario WHERE m.id_membro = " + id;

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                return buildMembro(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Membro por ID: " + e.getMessage());
        }
        return null;
    }

    public boolean existsByUsuarioId(int usuarioId) {
        String sql = "SELECT 1 FROM membro WHERE id_usuario = " + usuarioId;
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                return true; // Encontrou
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar existência de Membro por Usuário: " + e.getMessage());
        }
        return false; // Não encontrou
    }
}