package com.example.dminfo.controller;

import com.example.dminfo.model.DistribuicaoDeRecursos;
import com.example.dminfo.model.Recurso;
import com.example.dminfo.model.RecursoHasDistribuicaoDeRecursos;
import com.example.dminfo.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecursoHasDistribuicaoDeRecursosController {

    @Autowired
    private RecursoHasDistribuicaoDeRecursos itemDistribuicaoModel;

    @Autowired
    private RecursoController recursoController;

    @Autowired
    private DistribuicaoDeRecursosController distribuicaoController;

    public List<RecursoHasDistribuicaoDeRecursos> listar() {
        return itemDistribuicaoModel.listar("", SingletonDB.getConexao());
    }

    public List<RecursoHasDistribuicaoDeRecursos> listarPorDistribuicao(Integer idDistribuicao) {
        if (idDistribuicao == null || idDistribuicao <= 0) {
            throw new RuntimeException("ID da Distribuição é inválido.");
        }
        return itemDistribuicaoModel.listarPorDistribuicao(idDistribuicao, SingletonDB.getConexao());
    }

    public RecursoHasDistribuicaoDeRecursos getById(Integer idRecurso, Integer idDistribuicao) {
        if (idRecurso == null || idRecurso <= 0 || idDistribuicao == null || idDistribuicao <= 0) {
            throw new RuntimeException("IDs inválidos para a busca (Recurso e Distribuição).");
        }

        RecursoHasDistribuicaoDeRecursos item = itemDistribuicaoModel.getByIds(idRecurso, idDistribuicao, SingletonDB.getConexao());
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

        DistribuicaoDeRecursos d = distribuicaoController.getById(item.getDistribuicao());
        Recurso r = recursoController.getById(item.getRecurso());

        if (r.getQuantidade() < item.getQuantidade()) {
            throw new RuntimeException("Estoque insuficiente para adicionar este item à distribuição.");
        }

        r.setQuantidade(r.getQuantidade() - item.getQuantidade());
        recursoController.atualizar(r);

        return itemDistribuicaoModel.gravar(item, SingletonDB.getConexao());
    }

    public boolean atualizar(RecursoHasDistribuicaoDeRecursos item) {
        if (item == null || item.getRecurso() <= 0 || item.getDistribuicao() <= 0 || item.getQuantidade() <= 0) {
            throw new RuntimeException("Dados inválidos para atualização.");
        }

        RecursoHasDistribuicaoDeRecursos existente = this.getById(item.getRecurso(), item.getDistribuicao());
        Recurso r = recursoController.getById(item.getRecurso());

        int diferenca = item.getQuantidade() - existente.getQuantidade();

        if (diferenca > 0 && r.getQuantidade() < diferenca) {
            throw new RuntimeException("Estoque insuficiente para o acréscimo solicitado.");
        }

        r.setQuantidade(r.getQuantidade() - diferenca);
        recursoController.atualizar(r);

        return itemDistribuicaoModel.alterar(item, SingletonDB.getConexao());
    }

    public void excluir(Integer idRecurso, Integer idDistribuicao) {
        if (idRecurso == null || idRecurso <= 0 || idDistribuicao == null || idDistribuicao <= 0) {
            throw new RuntimeException("IDs inválidos para a exclusão.");
        }

        RecursoHasDistribuicaoDeRecursos existente = this.getById(idRecurso, idDistribuicao);

        Recurso r = recursoController.getById(idRecurso);
        r.setQuantidade(r.getQuantidade() + existente.getQuantidade());
        recursoController.atualizar(r);

        itemDistribuicaoModel.excluir(idRecurso, idDistribuicao, SingletonDB.getConexao());
    }
}