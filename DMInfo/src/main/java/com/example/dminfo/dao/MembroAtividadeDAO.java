package com.example.dminfo.dao;

import com.example.dminfo.util.Conexao;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MembroAtividadeDAO {

    private Conexao conexao = SingletonDB.getConexao();

    private Conexao getConexaoAtiva() {
        this.conexao = SingletonDB.getConexao();
        if (this.conexao == null || !this.conexao.getEstadoConexao()) {
            return null;
        }
        return this.conexao;
    }

    public List<Integer> listarMembrosPorAtividade(int idCriacao) {
        Conexao conn = getConexaoAtiva();
        if (conn == null) return new ArrayList<>();

        String sql = "SELECT membro_id_membro FROM criar_realizacao_atividades_membro WHERE criar_realizacao_atividades_id_criacao = " + idCriacao;
        ResultSet rs = conn.consultar(sql);
        List<Integer> idsMembros = new ArrayList<>();

        try {
            while (rs != null && rs.next()) {
                idsMembros.add(rs.getInt("membro_id_membro"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar membros por atividade: " + e.getMessage());
        }
        return idsMembros;
    }

    public boolean adicionarMembroAtividade(int idCriacao, int idMembro) {
        Conexao conn = getConexaoAtiva();
        if (conn == null) return false;

        // Verifica se a associação já existe (chave primária composta)
        String checkSql = String.format("SELECT 1 FROM criar_realizacao_atividades_membro WHERE criar_realizacao_atividades_id_criacao = %d AND membro_id_membro = %d", idCriacao, idMembro);
        try (ResultSet rs = conn.consultar(checkSql)) {
            if (rs != null && rs.next()) return true; // Já existe
        } catch (SQLException e) {
            System.err.println("Erro ao verificar associação: " + e.getMessage());
            return false;
        }

        // Insere com statusfrequencia padrão 'P' (Pendente)
        String sql = String.format("INSERT INTO criar_realizacao_atividades_membro (criar_realizacao_atividades_id_criacao, membro_id_membro, statusfrequencia) VALUES (%d, %d, '%s')",
                idCriacao, idMembro, "P");

        return conn.manipular(sql);
    }

    public boolean removerMembroAtividade(int idCriacao, int idMembro) {
        Conexao conn = getConexaoAtiva();
        if (conn == null) return false;

        String sql = String.format("DELETE FROM criar_realizacao_atividades_membro WHERE criar_realizacao_atividades_id_criacao = %d AND membro_id_membro = %d", idCriacao, idMembro);
        return conn.manipular(sql);
    }
}