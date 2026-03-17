package com.example.dminfo.controller;

import com.example.dminfo.model.ItemDistribuido;
import com.example.dminfo.model.DistribuicaoDeRecursos;
import com.example.dminfo.model.Recurso;
import com.example.dminfo.model.RecursoHasDistribuicaoDeRecursos;
import com.example.dminfo.util.Conexao;
import com.example.dminfo.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistribuicaoDeRecursosController {

    @Autowired
    private DistribuicaoDeRecursos distribuicaoModel;

    @Autowired
    private Recurso recursoModel;

    @Autowired
    private RecursoHasDistribuicaoDeRecursos recursoHasDistribuicaoDeRecursosModel;

    public List<DistribuicaoDeRecursos> listar() {
        return distribuicaoModel.listar(SingletonDB.getConexao());
    }

    public DistribuicaoDeRecursos salvar(DistribuicaoDeRecursos distribuicao) {
        Conexao conexao = SingletonDB.getConexao();

        if (distribuicao == null || distribuicao.getDescricao() == null || distribuicao.getDescricao().isEmpty()) {
            throw new RuntimeException("Objeto DistribuicaoDeRecursos inconsistente (descrição).");
        }

        if (distribuicao.getAdmin() <= 0) {
            throw new RuntimeException("Administrador inválido para a distribuição.");
        }
        if (distribuicao.getData() == null) {
            throw new RuntimeException("A data da distribuição não pode ser nula.");
        }

        DistribuicaoDeRecursos existente = distribuicaoModel.consultar(distribuicao, conexao);
        if (existente != null) {
            throw new RuntimeException("Distribuição já existe (mesma descrição).");
        }

        return distribuicaoModel.gravar(distribuicao, conexao);
    }

    public DistribuicaoDeRecursos atualizar(DistribuicaoDeRecursos distribuicao) {
        if (distribuicao == null ||
                distribuicao.getId() <= 0 ||
                distribuicao.getDescricao() == null ||
                distribuicao.getDescricao().isEmpty()) {
            throw new RuntimeException("Objeto DistribuicaoDeRecursos inconsistente ou ID inválido.");
        }

        if (distribuicao.getAdmin() <= 0) {
            throw new RuntimeException("Administrador inválido para a distribuição.");
        }
        if (distribuicao.getData() == null) {
            throw new RuntimeException("A data da distribuição não pode ser nula.");
        }

        distribuicaoModel.alterar(distribuicao, SingletonDB.getConexao());
        return distribuicao;
    }

    public DistribuicaoDeRecursos getById(Integer id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("ID inválido.");
        }
        DistribuicaoDeRecursos distribuicao = distribuicaoModel.getById(id, SingletonDB.getConexao());
        if (distribuicao == null) {
            throw new RuntimeException("Distribuição não encontrada.");
        }
        return distribuicao;
    }

    public void excluir(Integer id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("ID inválido para exclusão.");
        }

        DistribuicaoDeRecursos existente = distribuicaoModel.getById(id, SingletonDB.getConexao());
        if (existente == null) {
            throw new RuntimeException("Distribuição não encontrada, exclusão falhou.");
        }

        // Voltar as quantidades para os recursos
        List<RecursoHasDistribuicaoDeRecursos> itensVinculados = recursoHasDistribuicaoDeRecursosModel.listarPorDistribuicao(id, SingletonDB.getConexao());

        if (itensVinculados != null && !itensVinculados.isEmpty()) {
            for (RecursoHasDistribuicaoDeRecursos item : itensVinculados) {
                Recurso recurso = recursoModel.getById(item.getRecurso(), SingletonDB.getConexao());

                if (recurso != null) {
                    recurso.setQuantidade(recurso.getQuantidade() + item.getQuantidade());
                    recursoModel.alterar(recurso, SingletonDB.getConexao());
                }

                recursoHasDistribuicaoDeRecursosModel.excluir(item.getRecurso(), id, SingletonDB.getConexao());
            }
        }

        // Exclusao principal
        distribuicaoModel.excluir(id, SingletonDB.getConexao());
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
        novaDistribuicao = distribuicaoModel.gravar(novaDistribuicao, SingletonDB.getConexao());

        // 2. Processa cada item do carrinho
        if (temItens) {
            for (ItemDistribuido itemDist : novaDistribuicao.getItens()) {

                // Busca o recurso para checar o estoque
                Recurso recurso = recursoModel.getById(itemDist.getIdRecurso(), SingletonDB.getConexao());
                if (recurso == null) {
                    throw new RuntimeException("Recurso ID " + itemDist.getIdRecurso() + " não encontrado.");
                }

                if (recurso.getQuantidade() < itemDist.getQuantidade()) {
                    throw new RuntimeException("Estoque insuficiente para: " + recurso.getDescricao());
                }

                // 3. Atualiza o estoque do Recurso
                recurso.setQuantidade(recurso.getQuantidade() - itemDist.getQuantidade());
                recursoModel.alterar(recurso, SingletonDB.getConexao());

                // 4. Cria o vínculo na tabela intermediária com os SETs corrigidos
                recursoHasDistribuicaoDeRecursosModel.gravarFragmentado(novaDistribuicao.getId(), recurso.getId(), itemDist.getQuantidade(), SingletonDB.getConexao());
            }
        }
    }
}