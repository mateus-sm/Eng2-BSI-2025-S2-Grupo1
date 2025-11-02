package com.example.dminfo.model;

import com.example.dminfo.dao.DoadorDAO;
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

    // Construtor sem ID (para 'gravar')
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


    public List<Doador> listar() {
        return dao.get("");
    }

    public Doador getById(Integer id) {
        return dao.get(id);
    }

    public Doador salvar(Doador doador) {
        //Validação (ex: documento único)
        if (dao.getByDocumento(doador.getDocumento()) != null)
            throw new RuntimeException("Já existe um doador com este documento.");
        return dao.gravar(doador);
    }

    public Doador atualizar(Integer id, Doador doadorDetalhes) {
        Doador doador = dao.get(id);
        if (doador == null)
            throw new RuntimeException("Doador não encontrado com id: " + id); //

        //Verifica se o documento foi alterado para um que já existe
        if (!doador.getDocumento().equals(doadorDetalhes.getDocumento()) && dao.getByDocumento(doadorDetalhes.getDocumento()) != null)
            throw new RuntimeException("O novo documento já pertence a outro doador.");

        //Atualiza os campos
        doador.setNome(doadorDetalhes.getNome());
        doador.setDocumento(doadorDetalhes.getDocumento());
        doador.setRua(doadorDetalhes.getRua());
        doador.setBairro(doadorDetalhes.getBairro());
        doador.setCidade(doadorDetalhes.getCidade());
        doador.setUf(doadorDetalhes.getUf());
        doador.setCep(doadorDetalhes.getCep());
        doador.setEmail(doadorDetalhes.getEmail());
        doador.setTelefone(doadorDetalhes.getTelefone());
        doador.setContato(doadorDetalhes.getContato());

        if (dao.alterar(doador))
            return doador;
        throw new RuntimeException("Erro ao atualizar doador no banco de dados.");
    }

    public boolean excluir(Integer id){
        if (dao.get(id) == null)
            throw new RuntimeException("Doador não encontrado com id: " + id);
        return dao.excluir(id); //
    }
}