package com.example.dminfo.dao;

import com.example.dminfo.model.Atividade;
import com.example.dminfo.model.Evento;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AtividadeDAO {
    private String escapeString(String input) {
        if (input == null) {
            return "NULL";
        }
        return "'" + input.replace("'", "''") + "'";
    }

    private Atividade buildAtividade(ResultSet rs) throws SQLException {

        Evento eventoMock = new Evento();
        eventoMock.setId(rs.getInt("id_evento"));

        return new Atividade(
                rs.getInt("id_atividade"),
                eventoMock,
                rs.getString("descricao")
        );
    }

    public List<Atividade> getPorEvento(int idEvento) {
        List<Atividade> atividades = new ArrayList<>();

        String sql = String.format(
                "SELECT id_atividade, id_evento, descricao FROM atividade WHERE id_evento = %d ORDER BY descricao ASC",
                idEvento
        );

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null)
                while (rs.next())
                    atividades.add(buildAtividade(rs));
        } catch (SQLException e) {
            System.out.println("Erro ao listar Atividades por Evento: " + e.getMessage());
        }
        return atividades;
    }

    public Atividade getById(Integer id) {
        String sql = "SELECT id_atividade, id_evento, descricao FROM atividade WHERE id_atividade = " + id;
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                return buildAtividade(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar atividade por ID: " + e.getMessage());
        }
        return null;
    }

}