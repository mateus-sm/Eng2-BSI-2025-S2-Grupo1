package com.example.dminfo.model;

import org.springframework.stereotype.Repository;

@Repository
public class Recurso {
    private int id;
    private int id_doacao;
    private String descricao;
    private String tipo;
    private int quantidade;

    public Recurso() { }

    public Recurso(String descricao, int id, int id_doacao, int quantidade, String tipo) {
        this.descricao = descricao;
        this.id = id;
        this.id_doacao = id_doacao;
        this.quantidade = quantidade;
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_doacao() {
        return id_doacao;
    }

    public void setId_doacao(int id_doacao) {
        this.id_doacao = id_doacao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public static class MembroErro {
        private String erro;

        public MembroErro(String erro) {
            this.erro = erro;
        }

        public String getErro() {
            return erro;
        }

        public void setErro(String erro) {
            this.erro = erro;
        }
    }
}
