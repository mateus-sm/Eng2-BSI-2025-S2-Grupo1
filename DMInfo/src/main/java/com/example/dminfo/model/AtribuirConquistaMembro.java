package com.example.dminfo.model;

import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class AtribuirConquistaMembro {
    private int id;
    private int id_admin;
    private int id_membro;
    private int id_conquista;
    private Date data;
    private String observacao;

    public AtribuirConquistaMembro() {
    }

    public AtribuirConquistaMembro(int id_admin, int id_membro, int id_conquista, Date data, String observacao) {
        this.id_admin = id_admin;
        this.id_membro = id_membro;
        this.id_conquista = id_conquista;
        this.data = data;
        this.observacao = observacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id_atribuir_conquista) {
        this.id = id_atribuir_conquista;
    }

    public int getId_admin() {
        return id_admin;
    }

    public void setId_admin(int id_admin) {
        this.id_admin = id_admin;
    }

    public int getId_membro() {
        return id_membro;
    }

    public void setId_membro(int id_membro) {
        this.id_membro = id_membro;
    }

    public int getId_conquista() {
        return id_conquista;
    }

    public void setId_conquista(int id_conquista) {
        this.id_conquista = id_conquista;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
