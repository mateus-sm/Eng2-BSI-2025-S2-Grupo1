package com.example.dminfo.model;

import java.util.Date;

public class Usuario {
    private String nome, senha, usuario, telefone, email, rua, cidade, bairro, uf, cpf;
    private Date dtIni, dtFim, dtNasc;

    public Usuario(String nome, String senha, String usuario, String telefone, String email, String rua, String cidade, String bairro, String uf, String cpf, Date dtIni, Date dtFim, Date dtNasc){
        this.nome = nome;
        this.senha = senha;
        this.usuario = usuario;
        this.telefone = telefone;
        this.email = email;
        this.rua = rua;
        this.cidade = cidade;
        this.bairro = bairro;
        this.uf = uf;
        this.cpf = cpf;
        this.dtIni = dtIni;
        this.dtFim = dtFim;
        this.dtNasc = dtNasc;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Date getDtIni() {
        return dtIni;
    }

    public void setDtIni(Date dtIni) {
        this.dtIni = dtIni;
    }

    public Date getDtFim() {
        return dtFim;
    }

    public void setDtFim(Date dtFim) {
        this.dtFim = dtFim;
    }

    public Date getDtNasc() {
        return dtNasc;
    }

    public void setDtNasc(Date dtNasc) {
        this.dtNasc = dtNasc;
    }
}
