package com.example.dminfo.dao;

import com.example.dminfo.model.Evento;
import com.example.dminfo.model.Administrador;
import com.example.dminfo.util.Conexao;
import com.example.dminfo.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventoDAO {

    @Autowired
    private AdministradorDAO adminDAO;

    @Autowired
    private Administrador administradorModel;

    private String escapeString(String input) {
        if (input == null)
            return "NULL";
        return "'" + input.replace("'", "''") + "'";
    }

    private Evento buildEvento(ResultSet rs) throws SQLException {
        int adminId = rs.getInt("id_admin");
        Administrador admin = administradorModel.getById(adminId, SingletonDB.getConexao());

        // se nao tiver admin com o id, usa o mock
        if (admin == null) {
            Administrador adminMock = new Administrador();
            adminMock.setId(adminId);
            admin = adminMock;
        }

        return new Evento(
                rs.getInt("id_evento"),
                admin,
                rs.getString("titulo"),
                rs.getString("descricao")
        );
    }

    public List<Evento> buscarEventos(String termoDescricao, String ordenarPor) {
        List<Evento> eventos = new ArrayList<>();

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

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null)
                while (rs.next())
                    eventos.add(buildEvento(rs));
        } catch (SQLException e) {
            System.out.println("Erro ao listar Eventos com filtros: " + e.getMessage());
        }
        return eventos;
    }

    @Deprecated
    public List<Evento> getAll() {
        return buscarEventos(null, "tituloAsc"); // Chama o novo método sem filtros
    }

    @Deprecated
    public List<Evento> getTodos() {
        return buscarEventos(null, "tituloAsc"); // Chama o novo método sem filtros
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

        //debug
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