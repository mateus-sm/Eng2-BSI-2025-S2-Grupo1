package com.example.dminfo.controller;

import com.example.dminfo.model.Recurso;
import com.example.dminfo.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecursoController {

    @Autowired
    private Recurso recursoModel;

    public List<Recurso> listar() {
        return recursoModel.listar("", SingletonDB.getConexao());
    }

    public Recurso salvar(Recurso recurso) {
        if (recurso == null || recurso.getDescricao() == null || recurso.getDescricao().isEmpty()) {
            throw new RuntimeException("Objeto Recurso inconsistente.");
        }

        Recurso existente = recursoModel.getById(recurso.getId(), SingletonDB.getConexao());
        if (existente != null) {
            throw new RuntimeException("Recurso já existe (mesma descrição).");
        }

        return recursoModel.gravar(recurso, SingletonDB.getConexao());
    }

    public boolean atualizar(Recurso recurso) {
        if (recurso == null || recurso.getId() <= 0 || recurso.getDescricao() == null || recurso.getDescricao().isEmpty()) {
            throw new RuntimeException("Objeto Recurso inconsistente ou ID inválido.");
        }

        recurso = recursoModel.alterar(recurso, SingletonDB.getConexao());
        return recurso != null;
    }

    public Recurso getById(Integer id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("ID inválido.");
        }
        Recurso recurso = recursoModel.getById(id, SingletonDB.getConexao());
        if (recurso == null) {
            throw new RuntimeException("Recurso não encontrado.");
        }
        return recurso;
    }

    public void excluir(Integer id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("ID inválido para exclusão.");
        }

        Recurso existente = recursoModel.getById(id, SingletonDB.getConexao());
        if (existente == null) {
            throw new RuntimeException("Recurso não encontrado, exclusão falhou.");
        }

        recursoModel.excluir(id, SingletonDB.getConexao());
    }
}