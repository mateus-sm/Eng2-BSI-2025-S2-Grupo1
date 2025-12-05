package com.example.dminfo.model;

import com.example.dminfo.dao.MembroDAO;
import com.example.dminfo.util.Conexao;
import com.example.dminfo.util.SingletonDB;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class LancarMembroAtivo {

    @JsonIgnore
    private MembroDAO membroDAO = new MembroDAO();

    public LancarMembroAtivo() {}

    public List<Membro> listarTodos() {
        List<Membro> lista = new ArrayList<>();
        Conexao conexao = SingletonDB.getConexao();

        ResultSet rs = membroDAO.listarTodosResultSet(conexao);

        try {
            if (rs != null) {
                while (rs.next()) {
                    lista.add(montarMembro(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar membros: " + e.getMessage());
        }
        return lista;
    }

    public void atualizarStatus(int id, Membro dados) {
        Conexao conexao = SingletonDB.getConexao();

        Membro membroBanco = null;
        try (ResultSet rs = membroDAO.getByIdResultSet(id, conexao)) {
            if (rs != null && rs.next()) {
                membroBanco = montarMembro(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar membro no banco: " + e.getMessage());
        }

        if (membroBanco == null) {
            throw new RuntimeException("Membro não encontrado.");
        }

        if (dados.getDtFim() != null && membroBanco.getDtIni() != null &&
                dados.getDtFim().isBefore(membroBanco.getDtIni())) {
            throw new RuntimeException("A Data Fim não pode ser anterior à Data Início (" + membroBanco.getDtIni() + ").");
        }

        membroBanco.setDtFim(dados.getDtFim());
        if (dados.getObservacao() != null) {
            membroBanco.setObservacao(dados.getObservacao());
        }

        if (!membroDAO.alterar(membroBanco,conexao)) {
            throw new RuntimeException("Erro ao salvar alteração no banco.");
        }
    }

    private Membro montarMembro(ResultSet rs) throws SQLException {
        Membro m = new Membro();
        m.setId(rs.getInt("id_membro"));

        if (rs.getDate("dtini") != null)
            m.setDtIni(rs.getDate("dtini").toLocalDate());

        if (rs.getDate("dtfim") != null)
            m.setDtFim(rs.getDate("dtfim").toLocalDate());

        m.setObservacao(rs.getString("observacao"));

        Usuario u = new Usuario();
        u.setId(rs.getInt("id_usuario"));
        try { u.setNome(rs.getString("nome")); } catch (SQLException ignore) {}

        m.setUsuario(u);
        return m;
    }
}