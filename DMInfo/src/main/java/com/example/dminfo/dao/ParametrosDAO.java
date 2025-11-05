package com.example.dminfo.dao;

import com.example.dminfo.model.Parametros;
import com.example.dminfo.util.SingletonDB; // Mantendo seu Singleton
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ParametrosDAO {

    // Helper de segurança (para corrigir SQL Injection)
    private String escapeString(String input) {
        if (input == null) {
            return "NULL"; // Retorna 'NULL' literal para o SQL
        }
        // Substitui ' por '' (padrão SQL) e coloca aspas ao redor
        return "'" + input.replace("'", "''") + "'";
    }

    // Helper para construir o objeto
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
                rs.getString("logotipogrande"),  // Nome correto do banco
                rs.getString("logotipopequeno") // Nome correto do banco
        );
    }

    /**
     * Retorna a (única) linha de parâmetros do banco.
     */
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

    /**
     * Conta quantas linhas de parâmetros existem (deve ser 0 ou 1)
     */
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

    /**
     * Grava a primeira linha de parâmetros
     */
    public Parametros gravar(Parametros p) {
        // CORRIGIDO: Nomes dos métodos (getLogotipogrande) e segurança (escapeString)
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

    /**
     * Altera a linha de parâmetros existente
     */
    public boolean alterar(Parametros p) {
        // CORRIGIDO: Nomes dos métodos (getLogotipogrande) e segurança (escapeString)
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