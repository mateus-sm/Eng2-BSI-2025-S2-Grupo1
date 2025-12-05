package com.example.dminfo.dao;

import com.example.dminfo.model.Mensalidade;
import com.example.dminfo.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@Repository
public class MensalidadeDAO {

    // O método buildObject FOI REMOVIDO daqui e foi para o Model.

    public Mensalidade gravar(Mensalidade m, Conexao conexao){
        String sql = "INSERT INTO mensalidade (id_membro, mes, ano, valor, datapagamento) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, m.getId_membro());
            stmt.setInt(2, m.getMes());
            stmt.setInt(3, m.getAno());
            stmt.setDouble(4, m.getValor());
            stmt.setObject(5, m.getDataPagamento());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                m.setId_mensalidade(rs.getInt(1));
            }
            return m;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao gravar mensalidade: " + e.getMessage());
        }
    }

    public boolean alterar(Mensalidade m, Conexao conexao) {
        String sql = "UPDATE mensalidade SET id_membro = ?, mes = ?, ano = ?, valor = ?, datapagamento = ? " +
                "WHERE id_mensalidade = ?";

        try (PreparedStatement stmt = conexao.getConnection().prepareStatement(sql)) {

            stmt.setInt(1, m.getId_membro());
            stmt.setInt(2, m.getMes());
            stmt.setInt(3, m.getAno());
            stmt.setDouble(4, m.getValor());
            stmt.setObject(5, m.getDataPagamento());
            stmt.setInt(6, m.getId_mensalidade());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluir(Integer id, Conexao conexao) {
        String sql = "DELETE FROM mensalidade WHERE id_mensalidade = " + id;
        return conexao.manipular(sql);
    }

    // --- MÉTODOS DE BUSCA (AGORA RETORNAM RESULTSET) ---

    public ResultSet listar(String filtroNome, Conexao conexao) {
        String sql = """
        SELECT m.*, u.nome AS nome_membro
        FROM mensalidade m
        JOIN membro mem ON m.id_membro = mem.id_membro
        JOIN usuario u ON mem.id_usuario = u.id_usuario
        """;

        if (filtroNome != null && !filtroNome.isEmpty()) {
            sql += " WHERE u.nome ILIKE '%" + filtroNome + "%'";
        }

        sql += " ORDER BY m.datapagamento DESC";

        return conexao.consultar(sql);
    }

    public ResultSet buscarPorId(Integer id, Conexao conexao) {
        String sql = """
        SELECT m.*, u.nome AS nome_membro
        FROM mensalidade m
        JOIN membro mem ON m.id_membro = mem.id_membro
        JOIN usuario u ON mem.id_usuario = u.id_usuario
        WHERE m.id_mensalidade = %d
        """.formatted(id);

        return conexao.consultar(sql);
    }

    public ResultSet listarMesAno(int mes, int ano, Conexao conexao) {
        String sql = """
                SELECT m.*, u.nome AS nome_membro
                FROM mensalidade m
                JOIN membro mem ON m.id_membro = mem.id_membro
                JOIN usuario u ON mem.id_usuario = u.id_usuario
                WHERE m.mes = %d AND m.ano = %d
                ORDER BY m.datapagamento DESC
                """.formatted(mes, ano);

        return conexao.consultar(sql);
    }

    public ResultSet listarMembro(int idMembro, Conexao conexao) {
        String sql = """
                SELECT m.*, u.nome AS nome_membro
                FROM mensalidade m
                JOIN membro mem ON m.id_membro = mem.id_membro
                JOIN usuario u ON mem.id_usuario = u.id_usuario
                WHERE m.id_membro = %d
                ORDER BY m.datapagamento DESC
                """.formatted(idMembro);

        return conexao.consultar(sql);
    }

    public ResultSet filtrar(String nome, String dataIni, String dataFim, Conexao conexao) {
        String sql = """
        SELECT m.*, u.nome AS nome_membro
        FROM mensalidade m
        JOIN membro mem ON m.id_membro = mem.id_membro
        JOIN usuario u ON mem.id_usuario = u.id_usuario
        WHERE 1=1
        """;

        if (nome != null && !nome.isEmpty()) {
            sql += " AND u.nome ILIKE '%" + nome + "%'";
        }

        if (dataIni != null && !dataIni.isEmpty()) {
            sql += " AND m.datapagamento >= '" + dataIni + "'";
        }

        if (dataFim != null && !dataFim.isEmpty()) {
            sql += " AND m.datapagamento <= '" + dataFim + "'";
        }

        sql += " ORDER BY m.datapagamento DESC";

        return conexao.consultar(sql);
    }

    public ResultSet buscarPorMembroMesAno(int idMembro, int mes, int ano, Conexao conexao) {
        String sql = String.format("""
            SELECT m.*, u.nome AS nome_membro
            FROM mensalidade m
            JOIN membro mem ON m.id_membro = mem.id_membro
            JOIN usuario u ON mem.id_usuario = u.id_usuario
            WHERE m.id_membro = %d AND m.mes = %d AND m.ano = %d
            """, idMembro, mes, ano);

        return conexao.consultar(sql);
    }
}