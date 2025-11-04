//package com.example.dminfo.model;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "evento")
//public class Evento {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//
//    @Column(name = "id_evento")
//    private int id;
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "id_admin")
//    @JsonIgnore
//    private Administrador adm;
//
//    @Column(name = "titulo")
//    private String titulo;
//
//    @Column(name = "descricao")
//    private String descricao;
//
//    public Evento() {
//
//    }
//
//    public Evento(Administrador admin, String tit, String desc) {
//        this.adm = admin;
//        this.titulo = tit;
//        this.descricao = desc;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public Administrador getAdm() {
//        return adm;
//    }
//
//    public void setAdm(Administrador adm) {
//        this.adm = adm;
//    }
//
//    public String getTitulo() {
//        return titulo;
//    }
//
//    public void setTitulo(String titulo) {
//        this.titulo = titulo;
//    }
//
//    public String getDescricao() {
//        return descricao;
//    }
//
//    public void setDescricao(String descricao) {
//        this.descricao = descricao;
//    }
//}
