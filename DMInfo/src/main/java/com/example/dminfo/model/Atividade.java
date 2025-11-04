//package com.example.dminfo.model;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "atividade")
//public class Atividade {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//
//    @Column(name = "id_atividade")
//    private int id;
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "id_evento")
//    private Evento evento;
//
//    @Column(name = "descricao")
//    private String descricao;
//
//    public Atividade() {
//
//    }
//
//    public Atividade(int idatv, Evento ev, String desc){
//        this.id = idatv;
//        this.evento = ev;
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
//    public Evento getEvento() {
//        return evento;
//    }
//
//    public void setEvento(Evento evento) {
//        this.evento = evento;
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
