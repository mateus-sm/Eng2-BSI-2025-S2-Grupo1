package com.example.dminfo.dao;

import com.example.dminfo.model.RecursoHasDistribuicaoDeRecursos;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RecursoHasDistribuicaoDeRecursosDAO {

    private RecursoHasDistribuicaoDeRecursos buildItem(ResultSet rs) throws SQLException {
        RecursoHasDistribuicaoDeRecursos item = new RecursoHasDistribuicaoDeRecursos();

        int idRecurso = rs.getInt("recurso_id_recurso");
        int idDistribuicao = rs.getInt("distribuicao_de_recursos_id_distribuicao");

        item.setRecurso(idRecurso);
        item.setDistribuicao(idDistribuicao);

        item.setQuantidade(rs.getInt("quantidade"));

        return item;
    }
    public List<RecursoHasDistribuicaoDeRecursos> listar() {
        List<RecursoHasDistribuicaoDeRecursos> lista = new ArrayList<>();
        String sql = "SELECT * FROM recurso_has_distribuicao_de_recursos";
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            while (rs != null && rs.next()) {
                lista.add(buildItem(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar associações: " + e.getMessage());
        }
        return lista;
    }

    public List<RecursoHasDistribuicaoDeRecursos> listarPorDistribuicao(Integer idDistribuicao) {
        List<RecursoHasDistribuicaoDeRecursos> lista = new ArrayList<>();
        String sql = String.format("SELECT * FROM recurso_has_distribuicao_de_recursos WHERE distribuicao_de_recursos_id_distribuicao = %d", idDistribuicao);
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            while (rs != null && rs.next()) {
                lista.add(buildItem(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar por distribuição: " + e.getMessage());
        }
        return lista;
    }

    public RecursoHasDistribuicaoDeRecursos getById(Integer idRecurso, Integer idDistribuicao) {
        String sql = String.format(
                "SELECT * FROM recurso_has_distribuicao_de_recursos WHERE recurso_id_recurso = %d AND distribuicao_de_recursos_id_distribuicao = %d",
                idRecurso,
                idDistribuicao
        );
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                return buildItem(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar associação por ID: " + e.getMessage());
        }
        return null;
    }

    public RecursoHasDistribuicaoDeRecursos gravar(RecursoHasDistribuicaoDeRecursos item) {
        if (item == null)
            return null;

        String sql = String.format(
                "INSERT INTO recurso_has_distribuicao_de_recursos (recurso_id_recurso, distribuicao_de_recursos_id_distribuicao, quantidade) " +
                        "VALUES (%d, %d, %d)",
                item.getRecurso(),
                item.getDistribuicao(),
                item.getQuantidade()
        );

        if (SingletonDB.getConexao().manipular(sql)) {
            return item;
        } else {
            System.out.println("Erro ao gravar associação.");
            return null;
        }
    }

    public boolean alterar(RecursoHasDistribuicaoDeRecursos item) {
        if (item != null) {
            String sql = String.format(
                    "UPDATE recurso_has_distribuicao_de_recursos SET quantidade = %d " +
                            "WHERE recurso_id_recurso = %d AND distribuicao_de_recursos_id_distribuicao = %d",
                    item.getQuantidade(),
                    item.getRecurso(),
                    item.getDistribuicao()
            );

            return SingletonDB.getConexao().manipular(sql);
        }
        return false;
    }

    public boolean excluir(Integer idRecurso, Integer idDistribuicao) {
        String sql = String.format(
                "DELETE FROM recurso_has_distribuicao_de_recursos WHERE recurso_id_recurso = %d AND distribuicao_de_recursos_id_distribuicao = %d",
                idRecurso,
                idDistribuicao
        );
        return SingletonDB.getConexao().manipular(sql);
    }
}
