package com.example.dminfo.dao;

import com.example.dminfo.model.Calendario;
import com.example.dminfo.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CalendarioDAO implements IDAO<Calendario> {

    @Override
    public Calendario create(Calendario calendario, Conexao conexao) {
        if (calendario == null || calendario.getId_criacao() == null)
            return null;

        int idParaSalvar = calendario.getId_criacao().getId();

        String sqlCheck = "SELECT COUNT(*) FROM calendario WHERE id_criacao = " + idParaSalvar;

        try (ResultSet rs = conexao.consultar(sqlCheck)) {
            if (rs != null && rs.next() && rs.getInt(1) > 0)
                return calendario;

            String sqlInsert = String.format("INSERT INTO calendario (id_criacao) VALUES (%d)", idParaSalvar);
            conexao.manipular(sqlInsert);

            return calendario;

        } catch (SQLException e) {
            System.err.println("Erro SQL no CalendarioDAO ao criar: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean delete(int id, Conexao conexao) {
        if (conexao == null || !conexao.getEstadoConexao()) {
            return false;
        }
        String sql = "DELETE FROM calendario WHERE id_criacao = " + id;
        return conexao.manipular(sql);
    }

    public ResultSet listarTodasAtividades(Conexao conexao) {
        String sql = "SELECT cra.*, " +
                "u.usuario AS admin_usuario, " +
                "atv.descricao AS atividade_descricao, " +
                "c.id_criacao AS id_calendario_ativo " +
                "FROM criar_realizacao_atividades cra " +
                "JOIN administrador adm ON cra.id_admin = adm.id_admin " +
                "JOIN usuario u ON adm.id_usuario = u.id_usuario " +
                "JOIN atividade atv ON cra.id_atividade = atv.id_atividade " +
                "LEFT JOIN calendario c ON cra.id_criacao = c.id_criacao";

        return conexao.consultar(sql);
    }

    public ResultSet listarAtividadesAtivasIds(Conexao conexao) {
        String sql = "SELECT id_criacao FROM calendario";
        return conexao.consultar(sql);
    }

    //metodos IDAO n utilizados

    @Override
    public Calendario read(Calendario obj, Conexao conexao) {
        return null;
    }

    @Override
    public Calendario update(Calendario obj, Conexao conexao) {
        return null;
    }

    @Override
    public List<Calendario> readAll(String filtro, Conexao conexao) {
        return null;
    }
}