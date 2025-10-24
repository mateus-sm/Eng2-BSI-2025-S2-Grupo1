package com.example.dminfo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "conquista")
public class Conquista {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_conquista")
    private int id;

    @Column(name = "descricao")
    private String descricao;

    public Conquista() {
    }

    public Conquista(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
