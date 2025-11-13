package com.example.dminfo.model;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public class DistribuicaoDeRecursos {
    private int id;
    private Administrador admin;
    private LocalDate data;
    private String descricao;
    private String instituicaoReceptora;
    private double valor;

    public DistribuicaoDeRecursos() { }

    public DistribuicaoDeRecursos(Administrador admin, LocalDate data, String descricao, int id, String instituicaoReceptora, double valor) {
        this.admin = admin;
        this.data = data;
        this.descricao = descricao;
        this.id = id;
        this.instituicaoReceptora = instituicaoReceptora;
        this.valor = valor;
    }

    public Administrador getAdmin() {
        return admin;
    }

    public void setAdmin(Administrador admin) {
        this.admin = admin;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
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

    public String getInstituicaoReceptora() {
        return instituicaoReceptora;
    }

    public void setInstituicaoReceptora(String instituicaoReceptora) {
        this.instituicaoReceptora = instituicaoReceptora;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
