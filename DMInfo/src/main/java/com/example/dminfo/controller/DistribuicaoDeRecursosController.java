package com.example.dminfo.controller;

import com.example.dminfo.dao.DistribuicaoDeRecursosDAO;
import com.example.dminfo.model.DistribuicaoDeRecursos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistribuicaoDeRecursosController {

    @Autowired
    private DistribuicaoDeRecursosDAO distribuicaoDAO;

    public List<DistribuicaoDeRecursos> listar() {
        return distribuicaoDAO.listar();
    }

    public DistribuicaoDeRecursos salvar(DistribuicaoDeRecursos distribuicao) {
        if (distribuicao == null || distribuicao.getDescricao() == null || distribuicao.getDescricao().isEmpty()) {
            throw new RuntimeException("Objeto DistribuicaoDeRecursos inconsistente (descrição).");
        }

        if (distribuicao.getAdmin() <= 0) {
            throw new RuntimeException("Administrador inválido para a distribuição.");
        }
        if (distribuicao.getData() == null) {
            throw new RuntimeException("A data da distribuição não pode ser nula.");
        }

        DistribuicaoDeRecursos existente = distribuicaoDAO.consultar(distribuicao.getDescricao());
        if (existente != null) {
            throw new RuntimeException("Distribuição já existe (mesma descrição).");
        }

        return distribuicaoDAO.gravar(distribuicao);
    }

    public boolean atualizar(DistribuicaoDeRecursos distribuicao) {
        if (distribuicao == null || distribuicao.getId() <= 0 ||
                distribuicao.getDescricao() == null || distribuicao.getDescricao().isEmpty()) {
            throw new RuntimeException("Objeto DistribuicaoDeRecursos inconsistente ou ID inválido.");
        }

        if (distribuicao.getAdmin() <= 0) {
            throw new RuntimeException("Administrador inválido para a distribuição.");
        }
        if (distribuicao.getData() == null) {
            throw new RuntimeException("A data da distribuição não pode ser nula.");
        }

        return distribuicaoDAO.alterar(distribuicao);
    }

    public DistribuicaoDeRecursos getById(Integer id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("ID inválido.");
        }
        DistribuicaoDeRecursos distribuicao = distribuicaoDAO.getById(id);
        if (distribuicao == null) {
            throw new RuntimeException("Distribuição não encontrada.");
        }
        return distribuicao;
    }

    public void excluir(Integer id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("ID inválido para exclusão.");
        }

        DistribuicaoDeRecursos existente = distribuicaoDAO.getById(id);
        if (existente == null) {
            throw new RuntimeException("Distribuição não encontrada, exclusão falhou.");
        }

        distribuicaoDAO.excluir(id);
    }
}