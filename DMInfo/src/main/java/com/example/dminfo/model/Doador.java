package com.example.dminfo.model;

import org.springframework.stereotype.Component;

@Component
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

    public Doador(String nome, String documento, String rua, String bairro, String cidade, String uf, String cep, String email, String telefone, String contato) {
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