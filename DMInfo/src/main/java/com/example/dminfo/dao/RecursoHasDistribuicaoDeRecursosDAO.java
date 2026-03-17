package com.example.dminfo.dao;

import com.example.dminfo.model.RecursoHasDistribuicaoDeRecursos;
import com.example.dminfo.util.Conexao;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RecursoHasDistribuicaoDeRecursosDAO implements IDAO<RecursoHasDistribuicaoDeRecursos> {

    @Override
    public ResultSet readAll(String filtro, Conexao conexao) {
        String sql = "SELECT * FROM recurso_has_distribuicao_de_recursos";
        return conexao.consultar(sql);
    }

    @Override
    public ResultSet getById(int id, Conexao conexao) {
        String sql = String.format("SELECT * FROM recurso_has_distribuicao_de_recursos WHERE distribuicao_de_recursos_id_distribuicao = %d", id);
        return conexao.consultar(sql);
    }

    public ResultSet getByIds(Integer idRecurso, Integer idDistribuicao, Conexao conexao) {
        String sql = String.format(
                "SELECT * FROM recurso_has_distribuicao_de_recursos WHERE recurso_id_recurso = %d AND distribuicao_de_recursos_id_distribuicao = %d",
                idRecurso,
                idDistribuicao
        );
        return conexao.consultar(sql);
    }

    @Override
    public RecursoHasDistribuicaoDeRecursos create(RecursoHasDistribuicaoDeRecursos item, Conexao conexao) {
        if (item == null)
            return null;

        String sql = String.format(
                "INSERT INTO recurso_has_distribuicao_de_recursos (recurso_id_recurso, distribuicao_de_recursos_id_distribuicao, quantidade) " +
                        "VALUES (%d, %d, %d)",
                item.getRecurso(),
                item.getDistribuicao(),
                item.getQuantidade()
        );

        if (conexao.manipular(sql)) {
            return item;
        } else {
            System.out.println("Erro ao gravar associação.");
            return null;
        }
    }

    @Override
    public RecursoHasDistribuicaoDeRecursos update(RecursoHasDistribuicaoDeRecursos item, Conexao conexao) {
        if (item != null) {
            String sql = String.format(
                    "UPDATE recurso_has_distribuicao_de_recursos SET quantidade = %d " +
                            "WHERE recurso_id_recurso = %d AND distribuicao_de_recursos_id_distribuicao = %d",
                    item.getQuantidade(),
                    item.getRecurso(),
                    item.getDistribuicao()
            );

            if (conexao.manipular(sql)) {
                return item;
            } else {
                System.out.println("Erro ao gravar associação.");
                return null;
            }
        }

        return null;
    }

    public boolean deletes(int idRecurso, int idDistribuicao, Conexao conexao) {
        String sql = String.format(
                "DELETE FROM recurso_has_distribuicao_de_recursos WHERE recurso_id_recurso = %d AND distribuicao_de_recursos_id_distribuicao = %d",
                idRecurso,
                idDistribuicao
        );
        return conexao.manipular(sql);
    }

    @Override
    public boolean delete(int id, Conexao conexao) {
        return false;
    }

    @Override
    public RecursoHasDistribuicaoDeRecursos read(RecursoHasDistribuicaoDeRecursos rhdr, Conexao conexao) {
        return null;
    }
}
