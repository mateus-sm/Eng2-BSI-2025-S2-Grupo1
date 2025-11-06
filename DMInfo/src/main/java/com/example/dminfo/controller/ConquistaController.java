package com.example.dminfo.controller;

import com.example.dminfo.dao.ConquistaDAO;
import com.example.dminfo.model.Conquista;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConquistaController {

    @Autowired
    private ConquistaDAO conquistaDAO;

    public List<Conquista> listar() {
        return conquistaDAO.listar();
    }

    public Conquista salvar(Conquista conquista) {
        if (conquista == null || conquista.getDescricao() == null || conquista.getDescricao().isEmpty()) {
            throw new RuntimeException("Objeto Conquista inconsistente.");
        }

        Conquista existente = conquistaDAO.consultar(conquista.getDescricao());
        if (existente != null) {
            throw new RuntimeException("Conquista já existe.");
        }

        return conquistaDAO.gravar(conquista);
    }

    public boolean atualizar(Conquista conquista) {
        if (conquista == null || conquista.getDescricao() == null || conquista.getDescricao().isEmpty()) {
            throw new RuntimeException("Objeto Conquista inconsistente.");
        }

        return conquistaDAO.alterar(conquista);
    }

    public Conquista getById(Integer id) {
        Conquista conquista = conquistaDAO.getById(id);
        if (conquista == null) {
            throw new RuntimeException("Conquista não encontrada.");
        }
        return conquista;
    }

    public void excluir(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID inválido para exclusão.");
        }
        conquistaDAO.excluir(id);
    }
}
