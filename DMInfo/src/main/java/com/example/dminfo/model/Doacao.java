package com.example.dminfo.model;

import com.example.dminfo.dao.DoacaoDAO;
import com.example.dminfo.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class Doacao {

    private int id_doacao;
    private Doador id_doador;
    private Administrador id_admin;
    private LocalDate data;
    private double valor;
    private String observacao;

    @Autowired
    private DoacaoDAO dao;

    public Doacao() {}

    public Doacao(Doador id_doador, Administrador id_admin, LocalDate data, double valor, String observacao) {
        this.id_doador = id_doador;
        this.id_admin = id_admin;
        this.data = data;
        this.valor = valor;
        this.observacao = observacao;
    }

    public int getId_doacao() {return id_doacao;}
    public void setId_doacao(int id_doacao) {this.id_doacao = id_doacao;}
    public Doador getId_doador() {return id_doador;}
    public void setId_doador(Doador id_doador) {this.id_doador = id_doador;}
    public Administrador getId_admin() {return id_admin;}
    public void setId_admin(Administrador id_admin) {this.id_admin = id_admin;}
    public LocalDate getData() {return data;}
    public void setData(LocalDate data) {this.data = data;}
    public double getValor() {return valor;}
    public void setValor(double valor) {this.valor = valor;}
    public String getObservacao() {return observacao;}
    public void setObservacao(String observacao) {this.observacao = observacao;}


    public List<Doacao> listar(String filtro, Conexao conexao) {
        return dao.readAll(filtro, conexao);
    }

    public Doacao getById(Integer id, Conexao conexao) {
        return dao.getById(id, conexao);
    }

    public Doacao salvar(Doacao doacao, Conexao conexao) {
        if (doacao == null) {
            throw new RuntimeException("Dados da doação inválidos.");
        }

        if (doacao.getValor() <= 0) {
            throw new RuntimeException("O valor da doação deve ser positivo.");
        }

        doacao.setData(LocalDate.now());

        return dao.create(doacao, conexao);
    }

    public Doacao update(Integer id, Doacao doacaoDetalhes, Conexao conexao) {
        Doacao doacaoBanco = dao.getById(id, conexao);
        if (doacaoBanco == null) {
            throw new RuntimeException("Doação não encontrada para o ID: " + id);
        }

        if (doacaoDetalhes.getValor() <= 0) {
            throw new RuntimeException("O valor da doação deve ser positivo.");
        }

        doacaoBanco.setValor(doacaoDetalhes.getValor());
        doacaoBanco.setObservacao(doacaoDetalhes.getObservacao());

        if(doacaoDetalhes.getId_doador() != null)
            doacaoBanco.setId_doador(doacaoDetalhes.getId_doador());

        if(doacaoDetalhes.getId_admin() != null)
            doacaoBanco.setId_admin(doacaoDetalhes.getId_admin());

        Doacao d = dao.update(doacaoBanco, conexao);
        if (d != null) {
            return doacaoBanco;
        }
        throw new RuntimeException("Erro ao atualizar doação.");
    }

    public boolean excluir(Integer id, Conexao conexao) {
        Doacao d = dao.getById(id, conexao);
        if (d == null) {
            throw new RuntimeException("Doação não encontrada.");
        }
        return dao.delete(id, conexao);
    }
}