package com.example.dminfo.model;

import com.example.dminfo.dao.DoadorDAO;
import com.example.dminfo.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

    // Getters e Setters
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

    // --- Métodos de Lógica (Pattern do Projeto) ---

    public List<Doador> listar(String filtro, Conexao conexao) {
        return dao.readAll(filtro, conexao);
    }

    public Doador getById(Integer id, Conexao conexao) {
        return dao.getById(id, conexao);
    }

    public Doador getByDocumento(String documento, Conexao conexao) {
        return dao.getByDocumento(documento, conexao);
    }

    public Doador salvar(Doador doador, Conexao conexao) {
        if (doador == null) {
            throw new RuntimeException("Doador nulo");
        }
        return dao.create(doador, conexao);
    }

    public Doador alterar(Doador doador, Conexao conexao) {
        Doador d = dao.update(doador, conexao);
        if (d != null) {
            return doador;
        }
        throw new RuntimeException("Erro ao atualizar doador.");
    }

    public boolean excluir(Integer id, Conexao conexao) {
        Doador d = dao.getById(id, conexao);
        if (d == null) {
            throw new RuntimeException("Doador não encontrado.");
        }
        return dao.delete(id, conexao);
    }
}