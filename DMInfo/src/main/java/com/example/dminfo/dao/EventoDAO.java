package com.example.dminfo.dao;

import com.example.dminfo.model.Evento;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;

@Repository
public class EventoDAO {

    private String escapeString(String input) {
        if (input == null)
            return "NULL";
        return "'" + input.replace("'", "''") + "'";
    }

    public ResultSet buscarEventos(String termoDescricao, String ordenarPor) {
        String whereClause = "";
        if (termoDescricao != null && !termoDescricao.trim().isEmpty()) {
            String termo = termoDescricao.trim().replace("'", "''");
            whereClause = String.format(" WHERE descricao LIKE '%%%s%%'", termo);
        }

        String orderByClause = " ORDER BY titulo ASC"; // Padrão
        if ("idAsc".equalsIgnoreCase(ordenarPor)) {
            orderByClause = " ORDER BY id_evento ASC"; // Menor para o Maior (ID)
        } else if ("tituloAsc".equalsIgnoreCase(ordenarPor)) {
            orderByClause = " ORDER BY titulo ASC"; // Título (A-Z)
        }

        String sql = "SELECT id_evento, id_admin, titulo, descricao FROM evento" + whereClause + orderByClause;

        return SingletonDB.getConexao().consultar(sql);
    }

    @Deprecated
    public ResultSet getAll() {
        return buscarEventos(null, "tituloAsc"); // Chama o novo método sem filtros
    }

    @Deprecated
    public ResultSet getTodos() {
        return buscarEventos(null, "tituloAsc"); // Chama o novo método sem filtros
    }

    public ResultSet getById(Integer id) {
        String sql = "SELECT id_evento, id_admin, titulo, descricao FROM evento WHERE id_evento = " + id;
        return SingletonDB.getConexao().consultar(sql);
    }

    public Evento gravar(Evento evento) {
        String sql = String.format(
                "INSERT INTO evento (id_admin, titulo, descricao) VALUES (%d, %s, %s)",
                evento.getAdmin().getId(),
                escapeString(evento.getTitulo()),
                escapeString(evento.getDescricao())
        );

        System.out.println("SQL de Inserção: " + sql);

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