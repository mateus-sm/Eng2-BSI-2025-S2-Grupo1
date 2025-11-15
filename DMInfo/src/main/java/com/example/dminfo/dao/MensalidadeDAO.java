package com.example.dminfo.dao;

import com.example.dminfo.model.Mensalidade;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MensalidadeDAO {

    private Mensalidade buildObject(ResultSet rs) throws Exception {
        Mensalidade m = new Mensalidade();

        m.setId_mensalidade(rs.getInt("id_mensalidade"));
        m.setId_membro(rs.getInt("id_membro"));
        m.setMes(rs.getInt("mes"));
        m.setAno(rs.getInt("ano"));
        m.setValor(rs.getDouble("valor"));
        m.setDataPagamento(rs.getObject("dataPagamento", LocalDate.class));

        if (rs.getMetaData().getColumnCount() > 6) {
            m.setNomeMembro(rs.getString("nomeMembro"));
        }

        return m;
    }

    public Mensalidade gravar(Mensalidade m) {
        String sql = "INSERT INTO mensalidade (id_membro, mes, ano, valor, dataPagamento) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = SingletonDB.getConexao().getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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

    public boolean alterar(Mensalidade m) {
        String sql = "UPDATE mensalidade SET id_membro = ?, mes = ?, ano = ?, valor = ?, dataPagamento = ? " +
                "WHERE id_mensalidade = ?";

        try (PreparedStatement stmt = SingletonDB.getConexao().getConnection().prepareStatement(sql)) {

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

    public boolean excluir(Integer id) {
        String sql = "DELETE FROM mensalidade WHERE id_mensalidade = " + id;
        return SingletonDB.getConexao().manipular(sql);
    }

    public List<Mensalidade> listar(String filtroNome) {
        List<Mensalidade> lista = new ArrayList<>();
        String sql = "SELECT m.*, mem.nome as nomeMembro FROM mensalidade m " +
                "JOIN membro mem ON m.id_membro = mem.id_membro ";

        if (filtroNome != null && !filtroNome.isEmpty()) {
            sql += " WHERE mem.nome ILIKE '%" + filtroNome + "%'"; // ILIKE para case-insensitive (PostgreSQL)
        }

        sql += " ORDER BY m.dataPagamento DESC";

        try (ResultSet rs = SingletonDB.getConexao().consultar(sql)) {
            while (rs != null && rs.next()) {
                lista.add(buildObject(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Mensalidade buscarPorId(Integer id) {
        String sql = "SELECT m.*, mem.nome as nomeMembro FROM mensalidade m " +
                "JOIN membro mem ON m.id_membro = mem.id_membro " +
                "WHERE m.id_mensalidade = " + id;

        try (ResultSet rs = SingletonDB.getConexao().consultar(sql)) {
            if (rs != null && rs.next()) {
                return buildObject(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ... (resto da sua classe MensalidadeDAO)

    // Métodos de filtro (agora preenchidos)
    public List<Mensalidade> listarMesAno(int mes, int ano) {
        List<Mensalidade> lista = new ArrayList<>();
        // SQL com JOIN e filtro por mes e ano
        String sql = "SELECT m.*, mem.nome as nomeMembro FROM mensalidade m " +
                "JOIN membro mem ON m.id_membro = mem.id_membro " +
                "WHERE m.mes = " + mes + " AND m.ano = " + ano +
                " ORDER BY m.dataPagamento DESC";

        try (ResultSet rs = SingletonDB.getConexao().consultar(sql)) {
            while (rs != null && rs.next()) {
                lista.add(buildObject(rs)); // Reutiliza o seu método buildObject
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao listar pagamentos por mês/ano: " + e.getMessage());
        }
        return lista;
    }

    public List<Mensalidade> listarMembro(int idMembro) {
        List<Mensalidade> lista = new ArrayList<>();
        // SQL com JOIN e filtro por id_membro
        String sql = "SELECT m.*, mem.nome as nomeMembro FROM mensalidade m " +
                "JOIN membro mem ON m.id_membro = mem.id_membro " +
                "WHERE m.id_membro = " + idMembro +
                " ORDER BY m.dataPagamento DESC";

        try (ResultSet rs = SingletonDB.getConexao().consultar(sql)) {
            while (rs != null && rs.next()) {
                lista.add(buildObject(rs)); // Reutiliza o seu método buildObject
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao listar pagamentos por membro: " + e.getMessage());
        }
        return lista;
    }

}