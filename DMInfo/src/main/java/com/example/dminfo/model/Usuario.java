package com.example.dminfo.model;

import java.time.LocalDate;
public class Usuario {

    private int id;
    private String nome;
    private String cpf;
    private String login;
    private String senha;
    private String telefone;
    private String email;
    private LocalDate dtnasc;
    private LocalDate dtini;
    private String rua;
    private String cidade;
    private String bairro;
    private String cep;
    private String uf;
    private String foto;
    private LocalDate dtfim;

    public Usuario() {}

    public Usuario(int id, String nome, String senha, String login, String telefone,
                   String email, String rua, String cidade, String bairro, String cep,
                   String uf, String cpf, LocalDate dtnasc, LocalDate dtini, LocalDate dtfim) {
        this.id = id;
        this.nome = nome;
        this.senha = senha;
        this.login = login;
        this.telefone = telefone;
        this.email = email;
        this.rua = rua;
        this.cidade = cidade;
        this.bairro = bairro;
        this.cep = cep;
        this.uf = uf;
        this.cpf = cpf;
        this.dtnasc = dtnasc;
        this.dtini = dtini;
        this.dtfim = dtfim;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getDtnasc() { return dtnasc; }
    public void setDtnasc(LocalDate dtnasc) { this.dtnasc = dtnasc; }
    public LocalDate getDtini() { return dtini; }
    public void setDtini(LocalDate dtini) { this.dtini = dtini; }
    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
    public LocalDate getDtfim() { return dtfim; }
    public void setDtfim(LocalDate dtfim) { this.dtfim = dtfim; }

}