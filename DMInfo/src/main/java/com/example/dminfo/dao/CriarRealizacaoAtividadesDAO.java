package com.example.dminfo.dao;

import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.example.dminfo.model.Administrador;
import com.example.dminfo.model.Atividade;
import com.example.dminfo.model.Usuario;
import com.example.dminfo.util.Conexao; // Import necessário
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CriarRealizacaoAtividadesDAO implements IDAO<CriarRealizacaoAtividades> {

    private String escapeString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "NULL";
        }
        return "'" + input.replace("'", "''") + "'";
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return "NULL";
        }
        return "'" + date.toString() + "'";
    }

    private String formatDouble(double value) {
        return String.valueOf(value);
    }

    private String formatDouble(Double value) {
        if (value == null) {
            return "NULL";
        }
        return String.valueOf(value);
    }

    private String formatBoolean(Boolean value) {
        if (value == null || !value) {
            return "'FALSE'";
        }
        return "'TRUE'";
    }

    private CriarRealizacaoAtividades buildAtividade(ResultSet rs) throws SQLException {
        Administrador admin = new Administrador();
        admin.setId(rs.getInt("id_admin"));

        try {
            Usuario usuarioAdmin = new Usuario();
            usuarioAdmin.setLogin(rs.getString("admin_usuario"));
            admin.setUsuario(usuarioAdmin);
        } catch (SQLException ignored) {}

        Atividade atividade = new Atividade();
        atividade.setId(rs.getInt("id_atividade"));
        try {
            atividade.setDescricao(rs.getString("atividade_descricao"));
        } catch (SQLException ignored) {}


        CriarRealizacaoAtividades cra = new CriarRealizacaoAtividades();
        cra.setId(rs.getInt("id_criacao"));
        cra.setAdmin(admin);
        cra.setAtv(atividade);

        cra.setHorario(rs.getTime("horario"));
        cra.setLocal(rs.getString("local"));
        cra.setObservacoes(rs.getString("observacoes"));

        if (rs.getDate("dtini") != null) {
            cra.setDtIni(rs.getDate("dtini").toLocalDate());
        }
        if (rs.getDate("dtfim") != null) {
            cra.setDtFim(rs.getDate("dtfim").toLocalDate());
        }

        cra.setCustoprevisto(rs.getDouble("custoprevisto"));
        cra.setCustoreal(rs.getDouble("custoreal"));
        cra.setStatus(rs.getBoolean("status"));

        return cra;
    }

    @Override
    public CriarRealizacaoAtividades create(CriarRealizacaoAtividades obj, Conexao conexao) {
        return null;
    }

    @Override
    public CriarRealizacaoAtividades read(CriarRealizacaoAtividades obj, Conexao conexao) {
        if(obj != null)
            return getById(obj.getId(), conexao);

        return null;
    }

    @Override
    public CriarRealizacaoAtividades update(CriarRealizacaoAtividades obj, Conexao conexao) {
        if (finalizarAtividade(obj, conexao))
            return obj;

        return null;
    }

    @Override
    public boolean delete(int id, Conexao conexao) {
        String sql = "DELETE FROM criar_realizacao_atividades WHERE id_criacao = " + id;
        return conexao.manipular(sql);
    }

    @Override
    public List<CriarRealizacaoAtividades> readAll(String filtro, Conexao conexao) {
        return listarTodas(conexao);
    }


    public List<CriarRealizacaoAtividades> listarTodas(Conexao conexao) {
        String sql = "SELECT cra.*, " +
                "u.usuario AS admin_usuario, " +
                "atv.descricao AS atividade_descricao " +
                "FROM criar_realizacao_atividades cra " +
                "JOIN administrador adm ON cra.id_admin = adm.id_admin " +
                "JOIN usuario u ON adm.id_usuario = u.id_usuario " +
                "JOIN atividade atv ON cra.id_atividade = atv.id_atividade";

        ResultSet rs = conexao.consultar(sql);
        List<CriarRealizacaoAtividades> atividades = new ArrayList<>();

        try {
            while (rs != null && rs.next()) {
                atividades.add(buildAtividade(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar atividades: " + e.getMessage());
        }
        return atividades;
    }

    public CriarRealizacaoAtividades getById(Integer id, Conexao conexao) {
        String sql = "SELECT cra.*, " +
                "u.usuario AS admin_usuario, " +
                "atv.descricao AS atividade_descricao " +
                "FROM criar_realizacao_atividades cra " +
                "JOIN administrador adm ON cra.id_admin = adm.id_admin " +
                "JOIN usuario u ON adm.id_usuario = u.id_usuario " +
                "JOIN atividade atv ON cra.id_atividade = atv.id_atividade " +
                "WHERE cra.id_criacao = " + id;

        ResultSet rs = conexao.consultar(sql);
        try {
            if (rs != null && rs.next()) {
                return buildAtividade(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar atividade por ID: " + e.getMessage());
        }
        return null;
    }

    public boolean finalizarAtividade(CriarRealizacaoAtividades atividade, Conexao conexao) {
        String sql = String.format("UPDATE criar_realizacao_atividades SET " +
                        "dtini = %s, dtfim = %s, custoreal = %s, observacoes = %s, status = %s " +
                        "WHERE id_criacao = %d",
                formatDate(atividade.getDtIni()),
                formatDate(atividade.getDtFim()),
                formatDouble(atividade.getCustoreal()),
                escapeString(atividade.getObservacoes()),
                formatBoolean(atividade.getStatus()),
                atividade.getId()
        );

        return conexao.manipular(sql);
    }
}