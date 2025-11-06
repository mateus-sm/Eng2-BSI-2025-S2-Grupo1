package com.example.dminfo.dao;

import com.example.dminfo.model.Atividade;
import com.example.dminfo.model.Evento;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

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