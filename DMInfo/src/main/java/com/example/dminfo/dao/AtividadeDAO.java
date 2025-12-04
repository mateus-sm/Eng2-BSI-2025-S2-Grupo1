package com.example.dminfo.dao;

import com.example.dminfo.model.Atividade;
import com.example.dminfo.model.Evento;
import com.example.dminfo.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AtividadeDAO {

    private Atividade buildAtividade(ResultSet rs) throws SQLException {
        Evento eventoMock = new Evento();
        eventoMock.setId(rs.getInt("id_evento"));

        return new Atividade(
                rs.getInt("id_atividade"),
                eventoMock,
                rs.getString("descricao")
        );
    }

    public Atividade get(int id, Conexao conexao) {
        String sql = "SELECT id_atividade, id_evento, descricao FROM atividade WHERE id_atividade = " + id;

        try {
            ResultSet rs = conexao.consultar(sql);
            if (rs != null && rs.next())
                return buildAtividade(rs);
        } catch (SQLException e) {
            System.out.println("Erro ao buscar atividade por ID: " + e.getMessage());
        }
        return null;
    }

    public List<Atividade> getPorEvento(int idEvento, Conexao conexao) {
        List<Atividade> atividades = new ArrayList<>();

        String sql = String.format(
                "SELECT id_atividade, id_evento, descricao FROM atividade WHERE id_evento = %d ORDER BY descricao ASC",
                idEvento
        );

        try {
            ResultSet rs = conexao.consultar(sql);
            if (rs != null)
                while (rs.next())
                    atividades.add(buildAtividade(rs));
        } catch (SQLException e) {
            System.out.println("Erro ao listar Atividades por Evento: " + e.getMessage());
        }
        return atividades;
    }
}