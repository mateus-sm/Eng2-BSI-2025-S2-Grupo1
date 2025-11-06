package com.example.dminfo.dao;

import com.example.dminfo.model.Evento;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventoDAO {

    private Evento buildEvento(ResultSet rs) throws SQLException {
        return new Evento(
                rs.getInt("id_evento"),
                rs.getInt("id_admin"),
                rs.getString("titulo"),
                rs.getString("descricao")
        );
    }

    public Evento get(int id) {
        String sql = "SELECT * FROM evento WHERE id_evento = " + id;
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next())
                return buildEvento(rs);
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Evento por ID: " + e.getMessage());
        }
        return null;
    }

    public List<Evento> get(String filtro) {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM evento " + filtro;
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null)
                while (rs.next())
                    eventos.add(buildEvento(rs));
        } catch (SQLException e) {
            System.out.println("Erro ao listar Eventos: " + e.getMessage());
        }
        return eventos;
    }

    public Evento gravar(Evento evento) {
        String sql = String.format(
                "INSERT INTO evento (id_admin, titulo, descricao) " +
                        "VALUES (%d, '%s', '%s') RETURNING id_evento",
                evento.getIdAdmin(), evento.getTitulo(), evento.getDescricao()
        );

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next()) {
                evento.setId(rs.getInt("id_evento"));
                return evento;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gravar Evento: " + e.getMessage());
        }
        return null;
    }

    public boolean alterar(Evento evento) {
        String sql = String.format(
                "UPDATE evento SET id_admin = %d, titulo = '%s', descricao = '%s' " +
                        "WHERE id_evento = %d",
                evento.getIdAdmin(), evento.getTitulo(), evento.getDescricao(), evento.getId()
        );
        return SingletonDB.getConexao().executar(sql);
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM evento WHERE id_evento = " + id;
        return SingletonDB.getConexao().executar(sql);
    }
}
