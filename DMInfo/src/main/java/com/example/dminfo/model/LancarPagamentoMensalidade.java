package com.example.dminfo.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "mensalidade")

public class LancarPagamentoMensalidade{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id_mensalidade")
    private int idMensalidade;

    @Column(name = "id_membro")
    private int idMembro;

    @Column(name = "mes")
    private int mes;

    @Column(name = "ano")
    private int ano;

    @Column(name = "valor")
    private double valor;

    @Column(name = "datapagamento")
    private LocalDate datapagamento;

    public LancarPagamentoMensalidade() {

    }

    public LancarPagamentoMensalidade(int idMensalidade, int  idMembro, int mes, int ano, double valor, LocalDate datapagamento) {
        this.idMensalidade = idMensalidade;
        this.idMembro = idMembro;
        this.mes = mes;
        this.ano = ano;
        this.valor = valor;
        this.datapagamento = datapagamento;
    }

    public int getIdMensalidade() {
        return idMensalidade;
    }

    public void setIdMensalidade(int idMensalidade) {
        this.idMensalidade = idMensalidade;
    }

    public int getIdMembro() {
        return idMembro;
    }

    public void setIdMembro(int idMembro) {
        this.idMembro = idMembro;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public LocalDate getDatapagamento() {
        return datapagamento;
    }

    public void setDatapagamento(LocalDate datapagamento) {
        this.datapagamento = datapagamento;
    }

}