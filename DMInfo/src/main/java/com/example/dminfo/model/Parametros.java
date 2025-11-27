package com.example.dminfo.model;

import com.example.dminfo.dao.ParametrosDAO;
import com.example.dminfo.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class Parametros {
    private int id;
    private String razaoSocial;
    private String nomeFantasia;
    private String descricao;
    private String rua;
    private String bairro;
    private String cidade;
    private String cep;
    private String uf;
    private String telefone;
    private String site;
    private String email;
    private String cnpj;
    private String logoGrande;
    private String logoPequeno;

    @Autowired
    private ParametrosDAO dao;

    public Parametros() {}

    public Parametros(int id, String razaoSocial, String nomeFantasia, String descricao, String rua, String bairro, String cidade, String cep, String uf, String telefone, String site, String email, String cnpj, String logoGrande, String logoPequeno) {
        this.id = id;
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

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }
    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getSite() { return site; }
    public void setSite(String site) { this.site = site; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getLogoGrande() { return logoGrande; }
    public void setLogoGrande(String logoGrande) { this.logoGrande = logoGrande; }
    public String getLogoPequeno() { return logoPequeno; }
    public void setLogoPequeno(String logoPequeno) { this.logoPequeno = logoPequeno; }



    public Parametros exibir(Conexao conexao) {
        return dao.get(conexao);
    }

    public Parametros salvar(Parametros parametro, Conexao conexao) {
        if (parametro.getRazaoSocial() == null || parametro.getRazaoSocial().trim().isEmpty())
            throw new RuntimeException("Razão Social é obrigatória.");
        if (parametro.getCnpj() == null || parametro.getCnpj().isEmpty())
            throw new RuntimeException("CNPJ é obrigatório.");

        Parametros existente = dao.get(conexao);

        if (existente == null)
            return dao.create(parametro, conexao);
        else {
            parametro.setId(existente.getId());
            Parametros atualizado = dao.update(parametro, conexao);
            if (atualizado != null)
                return atualizado;

        }
        throw new RuntimeException("Erro ao salvar parâmetros.");
    }

    public void excluir(Integer id, Conexao conexao) {
        dao.delete(id, conexao);
    }

    public boolean existeParametro(Conexao conexao) {
        return dao.count(conexao) > 0;
    }
}