package com.example.dminfo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "doacao")
public class Doacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_doacao")
    private int id_doacao;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_doador")
    private int id_doador;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin")
    private int id_admin;

    @Column(name = "data")
    private LocalDate data;

    @Column(name = "valor")
    private double valor;

    @Column(name = "observacao")
    private String observacao;

    public Doacao() {
    }

    public Doacao(int id_doacao, int id_doador, int id_admin, LocalDate data, double valor, String observacao) {
        this.id_doacao = id_doacao;
        this.id_doador = id_doador;
        this.id_admin = id_admin;
        this.data = data;
        this.valor = valor;
        this.observacao = observacao;
    }

    public int getId_doacao() {return id_doacao;}
    public void setId_doacao(int id_doacao) {this.id_doacao = id_doacao;}

    public int getId_doador() {return id_doador;}
    public void setId_doador(int id_doador) {this.id_doador = id_doador;}

    public int getId_admin() {return id_admin;}
    public void setId_admin(int id_admin) {this.id_admin = id_admin;}

    public LocalDate getData() {return data;}
    public void setData(LocalDate data) {this.data = data;}

    public double getValor() {return valor;}
    public void setValor(double valor) {this.valor = valor;}

    public String getObservacao() {return observacao;}
    public void setObservacao(String observacao) {this.observacao = observacao;}
}