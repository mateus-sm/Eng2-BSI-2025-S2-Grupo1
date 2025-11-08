package com.example.dminfo.model;

import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class Doacao {

    private int id_doacao;
    private Doador id_doador;
    private Administrador id_admin;
    private LocalDate data;
    private double valor;
    private String observacao;

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
}