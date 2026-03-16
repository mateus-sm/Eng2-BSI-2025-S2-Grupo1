package com.example.dminfo.dao;

import com.example.dminfo.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;

@Repository
public class AtividadeDAO {

    public ResultSet get(int id, Conexao conexao) {
        String sql = "SELECT id_atividade, id_evento, descricao FROM atividade WHERE id_atividade = " + id;
        return conexao.consultar(sql);
    }

    public ResultSet getPorEvento(int idEvento, Conexao conexao) {
        String sql = String.format(
                "SELECT id_atividade, id_evento, descricao FROM atividade WHERE id_evento = %d ORDER BY descricao ASC",
                idEvento
        );
        return conexao.consultar(sql);
    }
}