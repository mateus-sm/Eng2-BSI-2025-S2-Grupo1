package com.example.dminfo.model;

import com.example.dminfo.dao.DoadorDAO;
import com.example.dminfo.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class Doador {

    private int id;
    private String nome;
    private String documento;
    private String rua;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;
    private String email;
    private String telefone;
    private String contato;

    @Autowired
    private DoadorDAO dao;

    public Doador() {}

    public Doador(int id, String nome, String documento, String rua, String bairro, String cidade, String uf, String cep, String email, String telefone, String contato) {
        this.id = id;
        this.nome = nome;
        this.documento = documento;
        this.rua = rua;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.cep = cep;
        this.email = email;
        this.telefone = telefone;
        this.contato = contato;
    }

    private Doador montarDoador(ResultSet rs) throws SQLException {
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

    public List<Doador> listar(String filtro, Conexao conexao) {
        List<Doador> lista = new ArrayList<>();
        ResultSet rs = dao.readAll(filtro, conexao);

        try {
            if (rs != null) {
                while (rs.next()) {
                    lista.add(montarDoador(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar doadores: " + e.getMessage());
        }
        return lista;
    }

    public Doador getById(Integer id, Conexao conexao) {
        ResultSet rs = dao.getById(id, conexao);
        try {
            if (rs != null && rs.next()) {
                return montarDoador(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar doador por ID: " + e.getMessage());
        }
        return null;
    }

    public Doador getByDocumento(Doador doador, Conexao conexao) {
        ResultSet rs = dao.read(doador, conexao);
        try {
            if (rs != null && rs.next()) {
                return montarDoador(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar doador por documento: " + e.getMessage());
        }
        return null;
    }

    public Doador salvar(Doador doador, Conexao conexao) {
        if (doador == null || doador.getNome() == null || doador.getNome().isBlank()) {
            throw new RuntimeException("Dados do doador inválidos.");
        }

        Doador existente = this.getByDocumento(doador, conexao);
        if (existente != null) {
            throw new RuntimeException("Já existe um doador com este documento.");
        }

        return dao.create(doador, conexao);
    }

    public Doador update(Integer id, Doador doadorDetalhes, Conexao conexao) {
        Doador doadorBanco = this.getById(id, conexao);

        if (doadorBanco == null) {
            throw new RuntimeException("Doador não encontrado para o ID: " + id);
        }

        doadorBanco.setNome(doadorDetalhes.getNome());
        doadorBanco.setDocumento(doadorDetalhes.getDocumento());
        doadorBanco.setRua(doadorDetalhes.getRua());
        doadorBanco.setBairro(doadorDetalhes.getBairro());
        doadorBanco.setCidade(doadorDetalhes.getCidade());
        doadorBanco.setUf(doadorDetalhes.getUf());
        doadorBanco.setCep(doadorDetalhes.getCep());
        doadorBanco.setEmail(doadorDetalhes.getEmail());
        doadorBanco.setTelefone(doadorDetalhes.getTelefone());
        doadorBanco.setContato(doadorDetalhes.getContato());

        Doador d = dao.update(doadorBanco, conexao);
        if (d != null) {
            return doadorBanco;
        }
        throw new RuntimeException("Erro ao atualizar doador.");
    }

    public boolean excluir(Integer id, Conexao conexao) {
        if (this.getById(id, conexao) == null) {
            throw new RuntimeException("Doador não encontrado.");
        }
        return dao.delete(id, conexao);
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}
    public String getDocumento() {return documento;}
    public void setDocumento(String documento) {this.documento = documento;}
    public String getRua() {return rua;}
    public void setRua(String rua) {this.rua = rua;}
    public String getBairro() {return bairro;}
    public void setBairro(String bairro) {this.bairro = bairro;}
    public String getCidade() {return cidade;}
    public void setCidade(String cidade) {this.cidade = cidade;}
    public String getUf() {return uf;}
    public void setUf(String uf) {this.uf = uf;}
    public String getCep() {return cep;}
    public void setCep(String cep) {this.cep = cep;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public String getTelefone() {return telefone;}
    public void setTelefone(String telefone) {this.telefone = telefone;}
    public String getContato() {return contato;}
    public void setContato(String contato) {this.contato = contato;}
}