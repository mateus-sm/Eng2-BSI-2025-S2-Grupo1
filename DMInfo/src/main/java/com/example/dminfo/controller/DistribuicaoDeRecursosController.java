package com.example.dminfo.controller;

import com.example.dminfo.dao.DistribuicaoDeRecursosDAO;
import com.example.dminfo.dao.RecursoDAO;
import com.example.dminfo.dao.RecursoHasDistribuicaoDeRecursosDAO;
import com.example.dminfo.model.ItemDistribuido;
import com.example.dminfo.model.DistribuicaoDeRecursos;
import com.example.dminfo.model.Recurso;
import com.example.dminfo.model.RecursoHasDistribuicaoDeRecursos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistribuicaoDeRecursosController {

    @Autowired
    private DistribuicaoDeRecursosDAO distribuicaoDAO;

    @Autowired
    private RecursoDAO recursoDAO;

    @Autowired
    private RecursoHasDistribuicaoDeRecursosDAO recursoHasDistribuicaoDeRecursosDAO;

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

        // Voltar as quantidades para os recursos
        List<RecursoHasDistribuicaoDeRecursos> itensVinculados = recursoHasDistribuicaoDeRecursosDAO.listarPorDistribuicao(id);

        if (itensVinculados != null && !itensVinculados.isEmpty()) {
            for (RecursoHasDistribuicaoDeRecursos item : itensVinculados) {
                Recurso recurso = recursoDAO.getById(item.getRecurso());

                if (recurso != null) {
                    recurso.setQuantidade(recurso.getQuantidade() + item.getQuantidade());
                    recursoDAO.alterar(recurso);
                }

                recursoHasDistribuicaoDeRecursosDAO.excluir(item.getRecurso(), id);
            }
        }

        // Exclusao principal
        distribuicaoDAO.excluir(id);
    }

    public void salvarComItens(DistribuicaoDeRecursos novaDistribuicao) {
        // 1. valida o cabeçalho da Distribuição
        if (novaDistribuicao.getDescricao() == null || novaDistribuicao.getDescricao().isEmpty()) {
            throw new RuntimeException("Descrição inválida.");
        }

        boolean temItens = novaDistribuicao.getItens() != null && !novaDistribuicao.getItens().isEmpty();
        boolean temValor = novaDistribuicao.getValor() > 0;

        if (!temItens && !temValor) {
            throw new RuntimeException("A distribuição deve conter pelo menos um recurso físico ou um valor monetário.");
        }

        // Salva a distribuição pai no banco
        novaDistribuicao = distribuicaoDAO.gravar(novaDistribuicao);

        // 2. Processa cada item do carrinho
        if (temItens) {
            for (ItemDistribuido itemDist : novaDistribuicao.getItens()) {

                // Busca o recurso para checar o estoque
                Recurso recurso = recursoDAO.getById(itemDist.getIdRecurso());
                if (recurso == null) {
                    throw new RuntimeException("Recurso ID " + itemDist.getIdRecurso() + " não encontrado.");
                }

                if (recurso.getQuantidade() < itemDist.getQuantidade()) {
                    throw new RuntimeException("Estoque insuficiente para: " + recurso.getDescricao());
                }

                // 3. Atualiza o estoque do Recurso
                recurso.setQuantidade(recurso.getQuantidade() - itemDist.getQuantidade());
                recursoDAO.alterar(recurso);

                // 4. Cria o vínculo na tabela intermediária com os SETs corrigidos
                RecursoHasDistribuicaoDeRecursos vinculo = new RecursoHasDistribuicaoDeRecursos();

                vinculo.setDistribuicao(novaDistribuicao.getId());
                vinculo.setRecurso(recurso.getId());
                vinculo.setQuantidade(itemDist.getQuantidade());

                recursoHasDistribuicaoDeRecursosDAO.gravar(vinculo);
            }
        }
    }
}