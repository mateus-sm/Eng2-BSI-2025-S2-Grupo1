package com.example.dminfo.model;

import com.example.dminfo.dao.ConquistaDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class Conquista {
    private int id;
    private String descricao;

    @Autowired
    private ConquistaDAO dao;

    public Conquista() {}

    public Conquista(String descricao) {
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

    public List<Conquista> listar() {
        return dao.listar();
    }

    public Conquista getById(Integer id) {
        Conquista conquista = dao.getById(id);
        if (conquista == null) {
            throw new RuntimeException("Conquista não encontrada para o ID: " + id);
        }
        return conquista;
    }

    public Conquista salvar(Conquista conquista) {
        if (conquista == null || conquista.getDescricao() == null || conquista.getDescricao().isBlank()) {
            throw new RuntimeException("Descrição da conquista é obrigatória.");
        }

        Conquista existente = dao.consultar(conquista.getDescricao());
        if (existente != null) {
            throw new RuntimeException("Já existe uma conquista com essa descrição.");
        }

        return dao.gravar(conquista);
    }

    public Conquista update(Integer id, Conquista conquistaDetails) {
        Conquista conquistaBanco = dao.getById(id);
        if (conquistaBanco == null) {
            throw new RuntimeException("Conquista não encontrada para o ID: " + id);
        }

        conquistaBanco.setDescricao(conquistaDetails.getDescricao());

        if (dao.alterar(conquistaBanco)) {
            return conquistaBanco;
        }
        throw new RuntimeException("Erro ao atualizar conquista.");
    }

    public boolean excluir(Integer id) {
        Conquista conquista = dao.getById(id);
        if (conquista == null) {
            throw new RuntimeException("Conquista não encontrada.");
        }
        return dao.excluir(id);
    }
}
