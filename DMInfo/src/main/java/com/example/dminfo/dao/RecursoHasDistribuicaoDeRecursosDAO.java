package com.example.dminfo.dao;

import com.example.dminfo.model.RecursoHasDistribuicaoDeRecursos;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RecursoHasDistribuicaoDeRecursosDAO {
    private RecursoHasDistribuicaoDeRecursos buildItem(ResultSet rs) throws SQLException {
        RecursoHasDistribuicaoDeRecursos item = new RecursoHasDistribuicaoDeRecursos();

        int idRecurso = rs.getInt("recurso_id_recurso");
        int idDistribuicao = rs.getInt("distribuicao_de_recursos_id_distribuicao");

        RecursoDAO recursoDAO = new RecursoDAO();
        DistribuicaoDeRecursosDAO distDAO = new DistribuicaoDeRecursosDAO();

        item.setRecurso(recursoDAO.getById(idRecurso));
        item.setDistribuicao(distDAO.getById(idDistribuicao));

        item.setQuantidade(rs.getInt("quantidade"));

        return item;
    }
}
