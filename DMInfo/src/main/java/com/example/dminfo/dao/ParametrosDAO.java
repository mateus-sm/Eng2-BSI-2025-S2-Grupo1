package com.example.dminfo.dao;

import com.example.dminfo.model.Parametros;
import com.example.dminfo.util.SingletonDB; // Mantendo seu Singleton
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ParametrosDAO {
    private String escapeString(String input) {
        if (input == null) {
            return "NULL";
        }
        return "'" + input.replace("'", "''") + "'";
    }

    private Parametros buildParametros(ResultSet rs) throws SQLException {
        return new Parametros(
                rs.getInt("id_parametro"),
                rs.getString("razao_social"),
                rs.getString("nome_fantasia"),
                rs.getString("descricao"),
                rs.getString("rua"),
                rs.getString("bairro"),
                rs.getString("cidade"),
                rs.getString("cep"),
                rs.getString("uf"),
                rs.getString("telefone"),
                rs.getString("site"),
                rs.getString("email"),
                rs.getString("cnpj"),
                rs.getString("logotipogrande"),
                rs.getString("logotipopequeno")
        );
    }

    public Parametros get() {
        String sql = "SELECT * FROM parametros LIMIT 1";
        ResultSet rs = SingletonDB.getConexao().consultar(sql); // Mantido
        try {
            if (rs != null && rs.next()) {
                return buildParametros(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar parâmetros: " + e.getMessage());
        }
        return null;
    }

    public long count() {
        String sql = "SELECT COUNT(*) AS total FROM parametros";
        ResultSet rs = SingletonDB.getConexao().consultar(sql); // Mantido
        try {
            if (rs != null && rs.next()) {
                return rs.getLong("total");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao contar parâmetros: " + e.getMessage());
        }
        return 0;
    }

    public Parametros gravar(Parametros p) {
        String sql = String.format("INSERT INTO parametros " +
                        "(razao_social, nome_fantasia, descricao, rua, bairro, cidade, cep, uf, telefone, site, email, cnpj, logotipogrande, logotipopequeno) " +
                        "VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) RETURNING id_parametro",
                escapeString(p.getRazaoSocial()), escapeString(p.getNomeFantasia()), escapeString(p.getDescricao()),
                escapeString(p.getRua()), escapeString(p.getBairro()), escapeString(p.getCidade()), escapeString(p.getCep()),
                escapeString(p.getUf()), escapeString(p.getTelefone()), escapeString(p.getSite()), escapeString(p.getEmail()),
                escapeString(p.getCnpj()), escapeString(p.getLogoGrande()), escapeString(p.getLogoPequeno())
        );
        ResultSet rs = SingletonDB.getConexao().consultar(sql); // Mantido
        try {
            if (rs != null && rs.next()) {
                p.setId(rs.getInt("id_parametro"));
                return p;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gravar parâmetros: " + e.getMessage());
        }
        return null;
    }

    public boolean alterar(Parametros p) {
        String sql = String.format("UPDATE parametros SET " +
                        "razao_social = %s, nome_fantasia = %s, descricao = %s, rua = %s, bairro = %s, cidade = %s, " +
                        "cep = %s, uf = %s, telefone = %s, site = %s, email = %s, cnpj = %s, " +
                        "logotipogrande = %s, logotipopequeno = %s WHERE id_parametro = %d",
                escapeString(p.getRazaoSocial()), escapeString(p.getNomeFantasia()), escapeString(p.getDescricao()),
                escapeString(p.getRua()), escapeString(p.getBairro()), escapeString(p.getCidade()), escapeString(p.getCep()),
                escapeString(p.getUf()), escapeString(p.getTelefone()), escapeString(p.getSite()), escapeString(p.getEmail()),
                escapeString(p.getCnpj()), escapeString(p.getLogoGrande()), escapeString(p.getLogoPequeno()),
                p.getId()
        );
        return SingletonDB.getConexao().manipular(sql); // Mantido
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM parametros WHERE id_parametro = " + id;
        return SingletonDB.getConexao().manipular(sql); // Mantido
    }
}