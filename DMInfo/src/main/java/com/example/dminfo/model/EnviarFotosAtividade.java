package com.example.dminfo.model;

import com.example.dminfo.dao.EnviarFotosAtividadeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class EnviarFotosAtividade {

    private int id;
    private Membro membro;
    private Atividade atividade;
    private String foto;
    private LocalDate data;

    @Autowired
    private EnviarFotosAtividadeDAO dao;

    public EnviarFotosAtividade(){}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Membro getMembro() { return membro; }
    public void setMembro(Membro membro) { this.membro = membro; }
    public Atividade getAtividade() { return atividade; }
    public void setAtividade(Atividade atividade) { this.atividade = atividade; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public EnviarFotosAtividade gravar(EnviarFotosAtividade foto) {
        return dao.gravar(foto);
    }

    public EnviarFotosAtividade getById(int idFoto) {
        return dao.get(idFoto);
    }

    public List<EnviarFotosAtividade> listarPorAtividade(int idAtividade) {
        return dao.getPorAtividade(idAtividade);
    }

    public boolean alterar(EnviarFotosAtividade foto) {
        return dao.alterar(foto);
    }

    public boolean excluir(int idFoto) {
        return dao.excluir(idFoto);
    }
}