package com.example.dminfo.model;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity
@Table(name = "parametros")
public class Parametros {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parametro")
    private int id;

    @Column(name = "razao_social")
    private String razaoSocial;

    @Column(name = "nome_fantasia")
    private String nomeFantasia;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "rua")
    private String rua;

    @Column(name = "bairro")
    private String bairro;

    @Column(name = "cidade")
    private String cidade;

    @Column(name = "cep")
    private String cep;

    @Column(name = "uf")
    private String uf;

    @Column(name = "telefone")
    private String telefone;

    @Column(name = "site")
    private String site;

    @Column(name = "email")
    private String email;

    @Column(name = "cnpj")
    private String cnpj;

    @Column(name = "logotipogrande")
    private String logoGrande;

    @Column(name = "logotipopequeno")
    private String logoPequeno;

    public Parametros() {
    }

    public Parametros(String razaoSocial, String nomeFantasia, String descricao, String rua, String bairro, String cidade, String cep, String uf, String telefone, String site, String email, String cnpj, String logoGrande, String logoPequeno) {
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
        this.descricao = descricao;
        this.rua = rua;
        this.bairro = bairro;
        this.cidade = cidade;
        this.cep = cep;
        this.uf = uf;
        this.telefone = telefone;
        this.site = site;
        this.email = email;
        this.cnpj = cnpj;
        this.logoGrande = logoGrande;
        this.logoPequeno = logoPequeno;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getLogoGrande() {
        return logoGrande;
    }

    public void setLogoGrande(String logoGrande) {
        this.logoGrande = logoGrande;
    }

    public String getLogoPequeno() {
        return logoPequeno;
    }

    public void setLogoPequeno(String logoPequeno) {
        this.logoPequeno = logoPequeno;
    }
}
