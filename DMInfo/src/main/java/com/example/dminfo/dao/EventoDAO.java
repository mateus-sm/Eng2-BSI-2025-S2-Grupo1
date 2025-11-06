package com.example.dminfo.dao;

import com.example.dminfo.model.Evento;
import com.example.dminfo.model.Administrador; // Necessário para construir o objeto Evento
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class EventoDAO {

    private String escapeString(String input) {
        if (input == null) {
            return "NULL";
        }
        return "'" + input.replace("'", "''") + "'";
    }

    private Evento buildEvento(ResultSet rs) throws SQLException {
        Administrador adminMock = new Administrador();
        adminMock.setId(rs.getInt("id_admin"));

        return new Evento(
                rs.getInt("id_evento"),
                adminMock,
                rs.getString("titulo"),
                rs.getString("descricao")
        );
    }

    public Evento getById(Integer id) {
        String sql = "SELECT id_evento, id_admin, titulo, descricao FROM evento WHERE id_evento = " + id;
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                return buildEvento(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar evento por ID: " + e.getMessage());
        }
        return null;
    }
}