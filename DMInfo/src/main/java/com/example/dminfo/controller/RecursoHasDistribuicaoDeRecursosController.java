package com.example.dminfo.controller;

import com.example.dminfo.dao.RecursoHasDistribuicaoDeRecursosDAO;
import com.example.dminfo.model.DistribuicaoDeRecursos;
import com.example.dminfo.model.Recurso;
import com.example.dminfo.model.RecursoHasDistribuicaoDeRecursos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecursoHasDistribuicaoDeRecursosController {

    @Autowired
    private RecursoHasDistribuicaoDeRecursosDAO itemDistribuicaoDAO;
    @Autowired
    private RecursoController recursoController;
    @Autowired
    private DistribuicaoDeRecursosController distribuicaoController;

    public List<RecursoHasDistribuicaoDeRecursos> listar() {
        return itemDistribuicaoDAO.listar();
    }

    public List<RecursoHasDistribuicaoDeRecursos> listarPorDistribuicao(Integer idDistribuicao) {
        if (idDistribuicao == null || idDistribuicao <= 0) {
            throw new RuntimeException("ID da Distribuição é inválido.");
        }
        return itemDistribuicaoDAO.listarPorDistribuicao(idDistribuicao);
    }

    public RecursoHasDistribuicaoDeRecursos getById(Integer idRecurso, Integer idDistribuicao) {
        if (idRecurso == null || idRecurso <= 0 || idDistribuicao == null || idDistribuicao <= 0) {
            throw new RuntimeException("IDs inválidos para a busca (Recurso e Distribuição).");
        }

        RecursoHasDistribuicaoDeRecursos item = itemDistribuicaoDAO.getById(idRecurso, idDistribuicao);

        if (item == null) {
            throw new RuntimeException("Associação não encontrada.");
        }
        return item;
    }

    public RecursoHasDistribuicaoDeRecursos salvar(RecursoHasDistribuicaoDeRecursos item) {
        if (item == null || item.getRecurso() <= 0 || item.getDistribuicao() <= 0) {
            throw new RuntimeException("Objeto de associação inconsistente. Recurso e Distribuição são obrigatórios.");
        }
        if (item.getQuantidade() <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que zero.");
        }

        try {
            Recurso r = recursoController.getById(item.getRecurso());
            DistribuicaoDeRecursos d = distribuicaoController.getById(item.getDistribuicao());
        } catch (RuntimeException e) {

            throw new RuntimeException("Falha ao salvar: " + e.getMessage());
        }

        return itemDistribuicaoDAO.gravar(item);
    }

    public boolean atualizar(RecursoHasDistribuicaoDeRecursos item) {
        if (item == null || item.getRecurso() <= 0 || item.getDistribuicao() <= 0) {
            throw new RuntimeException("Objeto de associação inconsistente. IDs de Recurso e Distribuição são obrigatórios.");
        }
        if (item.getQuantidade() <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que zero.");
        }

        return itemDistribuicaoDAO.alterar(item);
    }

    public void excluir(Integer idRecurso, Integer idDistribuicao) {
        if (idRecurso == null || idRecurso <= 0 || idDistribuicao == null || idDistribuicao <= 0) {
            throw new RuntimeException("IDs inválidos para a exclusão (Recurso e Distribuição).");
        }

        RecursoHasDistribuicaoDeRecursos existente = itemDistribuicaoDAO.getById(idRecurso, idDistribuicao);
        if (existente == null) {
            throw new RuntimeException("Associação não encontrada, exclusão falhou.");
        }

        itemDistribuicaoDAO.excluir(idRecurso, idDistribuicao);
    }
}