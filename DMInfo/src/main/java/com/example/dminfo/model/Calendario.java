package com.example.dminfo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "calendario")
public class Calendario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_calendario")
    private int id_calendario;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_criacao")
    private int id_criacao;

    public Calendario() {
    }

    public Calendario(int id_criacao) {
        this.id_criacao = id_criacao;
    }

    public int getId_calendario() {return id_calendario;}
    public void setId_calendario(int id_calendario) {this.id_calendario = id_calendario;}

    public int getId_criacao() {return id_criacao;}
    public void setId_criacao(int id_criacao) {this.id_criacao = id_criacao;}
}