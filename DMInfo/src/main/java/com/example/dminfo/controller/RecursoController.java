package com.example.dminfo.controller;

import com.example.dminfo.dao.RecursoDAO;
import com.example.dminfo.model.Recurso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecursoController {

    @Autowired
    private RecursoDAO recursoDAO;

    public List<Recurso> listar() {
        return recursoDAO.listar();
    }

    public Recurso salvar(Recurso recurso) {
        if (recurso == null || recurso.getDescricao() == null || recurso.getDescricao().isEmpty()) {
            throw new RuntimeException("Objeto Recurso inconsistente.");
        }

        Recurso existente = recursoDAO.consultar(recurso.getId());
        if (existente != null) {
            throw new RuntimeException("Recurso já existe (mesma descrição).");
        }

        return recursoDAO.gravar(recurso);
    }

    public boolean atualizar(Recurso recurso) {
        if (recurso == null || recurso.getId() <= 0 || recurso.getDescricao() == null || recurso.getDescricao().isEmpty()) {
            throw new RuntimeException("Objeto Recurso inconsistente ou ID inválido.");
        }

        return recursoDAO.alterar(recurso);
    }

    public Recurso getById(Integer id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("ID inválido.");
        }
        Recurso recurso = recursoDAO.getById(id);
        if (recurso == null) {
            throw new RuntimeException("Recurso não encontrado.");
        }
        return recurso;
    }

    public void excluir(Integer id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("ID inválido para exclusão.");
        }

        Recurso existente = recursoDAO.getById(id);
        if (existente == null) {
            throw new RuntimeException("Recurso não encontrado, exclusão falhou.");
        }

        recursoDAO.excluir(id);
    }
}