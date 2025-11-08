package com.example.dminfo.dao;

import com.example.dminfo.model.Evento;
import com.example.dminfo.model.Administrador;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventoDAO {

    private String escapeString(String input) {
        if (input == null)
            return "NULL";
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

    public List<Evento> getAll() {
        List<Evento> eventos = new ArrayList<>();
        return getTodos();
    }

    public List<Evento> getTodos() {
        List<Evento> eventos = new ArrayList<>();

        String sql = "SELECT id_evento, id_admin, titulo, descricao FROM evento ORDER BY titulo ASC";

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null)
                while (rs.next())
                    eventos.add(buildEvento(rs));
        } catch (SQLException e) {
            System.out.println("Erro ao listar Todos os Eventos: " + e.getMessage());
        }
        return eventos;
    }

    public Evento getById(Integer id) {
        String sql = "SELECT id_evento, id_admin, titulo, descricao FROM evento WHERE id_evento = " + id;
        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next())
                return buildEvento(rs);
        } catch (SQLException e) {
            System.out.println("Erro ao buscar evento por ID: " + e.getMessage());
        }
        return null;
    }

    public Evento gravar(Evento evento) {
        String sql = String.format(
                "INSERT INTO evento (id_admin, titulo, descricao) VALUES (%d, %s, %s)",
                evento.getAdmin().getId(),
                escapeString(evento.getTitulo()),
                escapeString(evento.getDescricao())
        );

        boolean executou = SingletonDB.getConexao().manipular(sql);

        if (!executou) {
            System.out.println("Erro ao gravar Evento: " + SingletonDB.getConexao().getMensagemErro());
            throw new RuntimeException("Falha ao inserir o evento.");
        }

        int idGerado = SingletonDB.getConexao().getMaxPK("evento", "id_evento");
        evento.setId(idGerado);
        return evento;
    }

    public boolean alterar(Evento evento) {
        String sql = String.format(
                "UPDATE evento SET id_admin=%d, titulo=%s, descricao=%s WHERE id_evento=%d",
                evento.getAdmin().getId(),
                escapeString(evento.getTitulo()),
                escapeString(evento.getDescricao()),
                evento.getId()
        );
        boolean executou = SingletonDB.getConexao().manipular(sql);

        if (!executou)
            System.out.println("Erro ao alterar Evento: " + SingletonDB.getConexao().getMensagemErro());
        return executou;
    }

    public boolean excluir(Integer id) {
        String sql = "DELETE FROM evento WHERE id_evento = " + id;
        boolean executou = SingletonDB.getConexao().manipular(sql);

        if (!executou)
            System.out.println("Erro ao excluir Evento: " + SingletonDB.getConexao().getMensagemErro());
        return executou;
    }
}