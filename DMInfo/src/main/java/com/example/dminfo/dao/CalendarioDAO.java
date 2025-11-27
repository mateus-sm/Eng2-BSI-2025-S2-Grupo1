package com.example.dminfo.dao;

import com.example.dminfo.model.*;
import com.example.dminfo.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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


    public List<CriarRealizacaoAtividades> listarTodasAtividades(Conexao conexao) {
        String sql = "SELECT cra.*, " +
                "u.usuario AS admin_usuario, " +
                "atv.descricao AS atividade_descricao, " +
                "c.id_criacao AS id_calendario_ativo " +
                "FROM criar_realizacao_atividades cra " +
                "JOIN administrador adm ON cra.id_admin = adm.id_admin " +
                "JOIN usuario u ON adm.id_usuario = u.id_usuario " +
                "JOIN atividade atv ON cra.id_atividade = atv.id_atividade " +
                "LEFT JOIN calendario c ON cra.id_criacao = c.id_criacao";

        ResultSet rs = conexao.consultar(sql);
        List<CriarRealizacaoAtividades> atividades = new ArrayList<>();

        try {
            while (rs != null && rs.next()) {
                atividades.add(buildAtividade(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar atividades: " + e.getMessage());
        }
        return atividades;
    }

    public List<Integer> listarAtividadesAtivasIds(Conexao conexao) {
        String sql = "SELECT id_criacao FROM calendario";
        ResultSet rs = conexao.consultar(sql);
        List<Integer> ids = new ArrayList<>();

        try {
            while (rs != null && rs.next()) {
                ids.add(rs.getInt("id_criacao"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar IDs ativos: " + e.getMessage());
        }
        return ids;
    }

    private CriarRealizacaoAtividades buildAtividade(ResultSet rs) throws SQLException {
        Administrador admin = new Administrador();
        admin.setId(rs.getInt("id_admin"));

        Usuario usuarioAdmin = new Usuario();
        usuarioAdmin.setLogin(rs.getString("admin_usuario"));
        admin.setUsuario(usuarioAdmin);

        Atividade atividade = new Atividade();
        atividade.setId(rs.getInt("id_atividade"));
        atividade.setDescricao(rs.getString("atividade_descricao"));

        CriarRealizacaoAtividades cra = new CriarRealizacaoAtividades();
        cra.setId(rs.getInt("id_criacao"));
        cra.setAdmin(admin);
        cra.setAtv(atividade);
        cra.setHorario(rs.getTime("horario"));
        cra.setLocal(rs.getString("local"));
        cra.setObservacoes(rs.getString("observacoes"));

        if (rs.getDate("dtini") != null) cra.setDtIni(rs.getDate("dtini").toLocalDate());
        if (rs.getDate("dtfim") != null) cra.setDtFim(rs.getDate("dtfim").toLocalDate());

        cra.setCustoprevisto(rs.getDouble("custoprevisto"));
        cra.setCustoreal(rs.getDouble("custoreal"));
        cra.setStatus(rs.getBoolean("status"));

        return cra;
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