package com.example.dminfo.dao;

import com.example.dminfo.model.Doador;
import com.example.dminfo.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DoadorDAO {

    private Doador buildDoador(ResultSet rs) throws SQLException {
        return new Doador(
                rs.getInt("id_doador"),
                rs.getString("nome"),
                rs.getString("documento"),
                rs.getString("rua"),
                rs.getString("bairro"),
                rs.getString("cidade"),
                rs.getString("uf"),
                rs.getString("cep"),
                rs.getString("email"),
                rs.getString("telefone"),
                rs.getString("contato")
        );
    }

    public Doador getById(int id, Conexao conexao) {
        String sql = "SELECT * FROM doador WHERE id_doador = " + id;
        ResultSet rs = conexao.consultar(sql);
        try{
            if(rs != null && rs.next())
                return buildDoador(rs);
        }catch(SQLException e){
            System.out.println("Erro ao buscar Doador por ID: " + e.getMessage());
        }
        return null;
    }

    public Doador getByDocumento(String documento, Conexao conexao) {
        String documentoSeguro = documento.replace("'", "''");
        String sql = "SELECT * FROM doador WHERE documento = '" + documentoSeguro + "'";
        ResultSet rs = conexao.consultar(sql);
        try {
            if(rs != null && rs.next())
                return buildDoador(rs);
        }catch(SQLException e){
            System.out.println("Erro ao buscar Doador por Documento: " + e.getMessage());
        }
        return null;
    }

    public List<Doador> readAll(String filtro, Conexao conexao) {
        List<Doador> doadores = new ArrayList<>();
        // Aplica filtro se vier preenchido (ex: WHERE nome LIKE ...)
        String sql = "SELECT * FROM doador " + (filtro != null ? filtro : "");
        ResultSet rs = conexao.consultar(sql);
        try {
            if (rs != null)
                while (rs.next())
                    doadores.add(buildDoador(rs));
        }catch(SQLException e){
            System.out.println("Erro ao listar Doadores: " + e.getMessage());
        }
        return doadores;
    }

    public Doador create(Doador doador, Conexao conexao){
        String sql = String.format("INSERT INTO doador (nome, documento, rua, bairro, cidade, uf, cep, email, telefone, contato) " +
                        "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s') RETURNING id_doador",
                doador.getNome().replace("'", "''"),
                doador.getDocumento().replace("'", "''"),
                doador.getRua().replace("'", "''"),
                doador.getBairro().replace("'", "''"),
                doador.getCidade().replace("'", "''"),
                doador.getUf().replace("'", "''"),
                doador.getCep().replace("'", "''"),
                doador.getEmail().replace("'", "''"),
                doador.getTelefone().replace("'", "''"),
                doador.getContato().replace("'", "''")
        );

        ResultSet rs = conexao.consultar(sql);
        try{
            if (rs != null && rs.next()){
                doador.setId(rs.getInt("id_doador"));
                return doador;
            }
        }catch(SQLException e){
            System.out.println("Erro ao gravar Doador: " + e.getMessage());
        }
        return null;
    }

    public Doador update(Doador doador, Conexao conexao) {
        String sql = String.format("UPDATE doador SET nome = '%s', documento = '%s', rua = '%s', bairro = '%s', " +
                        "cidade = '%s', uf = '%s', cep = '%s', email = '%s', telefone = '%s', contato = '%s' " +
                        "WHERE id_doador = %d",
                doador.getNome().replace("'", "''"),
                doador.getDocumento().replace("'", "''"),
                doador.getRua().replace("'", "''"),
                doador.getBairro().replace("'", "''"),
                doador.getCidade().replace("'", "''"),
                doador.getUf().replace("'", "''"),
                doador.getCep().replace("'", "''"),
                doador.getEmail().replace("'", "''"),
                doador.getTelefone().replace("'", "''"),
                doador.getContato().replace("'", "''"),
                doador.getId()
        );

        conexao.consultar(sql);
        return doador;
    }

    public boolean delete(int id, Conexao conexao) {
        String sql = "DELETE FROM doador WHERE id_doador = " + id;
        return conexao.manipular(sql);
    }
}