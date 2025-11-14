package com.example.dminfo.model;

public class RecursoHasDistribuicaoDeRecursos {

    private Recurso recurso;
    private DistribuicaoDeRecursos distribuicao;
    private int quantidade;

    public RecursoHasDistribuicaoDeRecursos() { }

    public RecursoHasDistribuicaoDeRecursos(Recurso recurso, DistribuicaoDeRecursos distribuicao, int quantidade) {
        this.recurso = recurso;
        this.distribuicao = distribuicao;
        this.quantidade = quantidade;
    }

    public Recurso getRecurso() {
        return recurso;
    }

    public void setRecurso(Recurso recurso) {
        this.recurso = recurso;
    }

    public DistribuicaoDeRecursos getDistribuicao() {
        return distribuicao;
    }

    public void setDistribuicao(DistribuicaoDeRecursos distribuicao) {
        this.distribuicao = distribuicao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}