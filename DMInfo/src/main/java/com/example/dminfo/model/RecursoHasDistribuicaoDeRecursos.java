package com.example.dminfo.model;

public class RecursoHasDistribuicaoDeRecursos {

    private int recurso;
    private int distribuicao;
    private int quantidade;

    public RecursoHasDistribuicaoDeRecursos() { }

    public RecursoHasDistribuicaoDeRecursos(int recurso, int distribuicao, int quantidade) {
        this.recurso = recurso;
        this.distribuicao = distribuicao;
        this.quantidade = quantidade;
    }

    public int getRecurso() {
        return recurso;
    }

    public void setRecurso(int recurso) {
        this.recurso = recurso;
    }

    public int getDistribuicao() {
        return distribuicao;
    }

    public void setDistribuicao(int distribuicao) {
        this.distribuicao = distribuicao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}