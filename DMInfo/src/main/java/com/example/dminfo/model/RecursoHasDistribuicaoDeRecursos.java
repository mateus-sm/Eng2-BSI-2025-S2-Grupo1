package com.example.dminfo.model;

import com.example.dminfo.dao.RecursoHasDistribuicaoDeRecursosDAO;
import com.example.dminfo.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RecursoHasDistribuicaoDeRecursos {

    private int recurso;
    private int distribuicao;
    private int quantidade;

    @Autowired
    RecursoHasDistribuicaoDeRecursosDAO dao;

    public RecursoHasDistribuicaoDeRecursos() { }

    public RecursoHasDistribuicaoDeRecursos(int recurso, int distribuicao, int quantidade) {
        this.recurso = recurso;
        this.distribuicao = distribuicao;
        this.quantidade = quantidade;
    }

    public int getRecurso() {
        return recurso;
    }

    public void setRecurso(int recurso) {
        this.recurso = recurso;
    }

    public int getDistribuicao() {
        return distribuicao;
    }

    public void setDistribuicao(int distribuicao) {
        this.distribuicao = distribuicao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    // Métodos
    private RecursoHasDistribuicaoDeRecursos buildItem(ResultSet rs) throws SQLException {
        RecursoHasDistribuicaoDeRecursos item = new RecursoHasDistribuicaoDeRecursos();

        int idRecurso = rs.getInt("recurso_id_recurso");
        int idDistribuicao = rs.getInt("distribuicao_de_recursos_id_distribuicao");

        item.setRecurso(idRecurso);
        item.setDistribuicao(idDistribuicao);

        item.setQuantidade(rs.getInt("quantidade"));

        return item;
    }

    public List<RecursoHasDistribuicaoDeRecursos> listar(String filtro, Conexao conexao) {
        List<RecursoHasDistribuicaoDeRecursos> lista = new ArrayList<>();
        ResultSet rs = dao.readAll(filtro, conexao);

        try {
            while (rs != null && rs.next()) {
                lista.add(buildItem(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar associações: " + e.getMessage());
        }

        return lista;
    }

    public List<RecursoHasDistribuicaoDeRecursos> listarPorDistribuicao(Integer id, Conexao conexao) {
        List<RecursoHasDistribuicaoDeRecursos> lista = new ArrayList<>();
        ResultSet rs = dao.getById(id, conexao);

        try {
            while (rs != null && rs.next()) {
                lista.add(buildItem(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar por distribuição: " + e.getMessage());
        }

        return lista;
    }

    public RecursoHasDistribuicaoDeRecursos getByIds(Integer idRecurso, Integer idDistribuicao, Conexao conexao) {
        ResultSet rs = dao.getByIds(idRecurso, idDistribuicao, conexao);

        try {
            if (rs != null && rs.next()) {
                return buildItem(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar associação por IDs: " + e.getMessage());
        }

        return null;
    }

    public void gravarFragmentado(int idRec, int idDist, int qtd, Conexao conexao) {
        RecursoHasDistribuicaoDeRecursos vinculo = new RecursoHasDistribuicaoDeRecursos();
        vinculo.setDistribuicao(idDist);
        vinculo.setRecurso(idRec);
        vinculo.setQuantidade(qtd);
        gravar(vinculo, conexao);
    }

    public RecursoHasDistribuicaoDeRecursos gravar(RecursoHasDistribuicaoDeRecursos item, Conexao conexao) {
        if (item == null)
            return null;

        if (dao.create(item, conexao) != null) {
            return item;
        } else {
            System.out.println("Erro ao gravar associação.");
            return null;
        }
    }

    public boolean alterar(RecursoHasDistribuicaoDeRecursos item, Conexao conexao) {
        if (item != null) {
            return dao.update(item, conexao) != null;
        }
        return false;
    }

    public boolean excluir(Integer idRecurso, Integer idDistribuicao, Conexao conexao) {
        return dao.deletes(idRecurso, idDistribuicao, conexao);
    }
}