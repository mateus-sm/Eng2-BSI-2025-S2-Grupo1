package com.example.dminfo.model;

import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public class Mensalidade {

    private int id_mensalidade;
    private int id_membro;
    private int mes;
    private int ano;
    private Double valor;
    private LocalDate dataPagamento;

    private String nomeMembro;

    public Mensalidade() {}

    public Mensalidade(int id_mensalidade, int id_membro, int mes, int ano, Double valor, LocalDate dataPagamento) {
        this.id_mensalidade = id_mensalidade;
        this.id_membro = id_membro;
        this.mes = mes;
        this.ano = ano;
        this.valor = valor;
        this.dataPagamento = dataPagamento;
    }

    public int getId_mensalidade() { return id_mensalidade; }
    public void setId_mensalidade(int id_mensalidade) { this.id_mensalidade = id_mensalidade; }

    public int getId_membro() { return id_membro; }
    public void setId_membro(int id_membro) { this.id_membro = id_membro; }

    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }

    public String getNomeMembro() { return nomeMembro; }
    public void setNomeMembro(String nomeMembro) { this.nomeMembro = nomeMembro; }
}