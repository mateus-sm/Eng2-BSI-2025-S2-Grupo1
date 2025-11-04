package com.example.dminfo.model;

import com.example.dminfo.dao.ParametrosDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository // Não é mais @Entity
public class Parametros {

    // --- Campos POJO (iguais aos da entidade) ---
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

    // --- Injeção do DAO ---
    @Autowired
    private ParametrosDAO dao;

    // --- Construtores ---
    public Parametros() {}

    // Construtor completo para o DAO
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

    // Construtor sem ID (para 'gravar')
    public Parametros(String razaoSocial, String nomeFantasia, String descricao, String rua, String bairro, String cidade, String cep, String uf, String telefone, String site, String email, String cnpj, String logoGrande, String logoPequeno) {
        // ... (o mesmo do construtor antigo)
    }

    // --- Getters e Setters (iguais aos da entidade) ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }
    // ... (Cole TODOS os outros getters e setters da sua entidade original aqui) ...
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


    // --- LÓGICA DE NEGÓCIOS (do antigo ParametrosService) ---

    public Parametros exibir() {
        return dao.get(); // Retorna o único parâmetro
    }

    /**
     * Salva ou Atualiza a (única) linha de parâmetros.
     */
    public Parametros salvar(Parametros parametro) {
        // Validação (ex: CNPJ é obrigatório)
        if (parametro.getCnpj() == null || parametro.getCnpj().isEmpty()) {
            throw new RuntimeException("CNPJ é obrigatório.");
        }

        Parametros existente = dao.get();
        if (existente == null) {
            // Se não existe, cria (INSERT)
            return dao.gravar(parametro);
        } else {
            // Se já existe, atualiza (UPDATE)
            parametro.setId(existente.getId()); // Garante que estamos atualizando o ID correto
            if (dao.alterar(parametro)) {
                return parametro;
            }
        }
        throw new RuntimeException("Erro ao salvar parâmetros.");
    }

    public void excluir(Integer id) {
        dao.excluir(id);
    }

    public boolean existeParametro() {
        return dao.count() > 0;
    }
}