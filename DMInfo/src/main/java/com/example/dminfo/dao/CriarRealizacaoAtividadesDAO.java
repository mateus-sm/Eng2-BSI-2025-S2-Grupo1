package com.example.dminfo.dao;

import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.example.dminfo.model.Administrador;
import com.example.dminfo.model.Atividade;
import com.example.dminfo.model.Usuario;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CriarRealizacaoAtividadesDAO {

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

    private CriarRealizacaoAtividades buildAtividade(ResultSet rs) throws SQLException {
        Administrador admin = new Administrador();
        admin.setId(rs.getInt("id_admin"));

        Atividade atividade = new Atividade();
        atividade.setId(rs.getInt("id_atividade"));

        CriarRealizacaoAtividades cra = new CriarRealizacaoAtividades();
        cra.setId(rs.getInt("id_criacao")); // id_criacao
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

    public List<CriarRealizacaoAtividades> listarTodas() {
        String sql = "SELECT cra.*, " +
                "u.usuario AS admin_usuario, " +
                "atv.descricao AS atividade_descricao " +
                "FROM criar_realizacao_atividades cra " +
                "JOIN administrador adm ON cra.id_admin = adm.id_admin " +
                "JOIN usuario u ON adm.id_usuario = u.id_usuario " +
                "JOIN atividade atv ON cra.id_atividade = atv.id_atividade";

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        List<CriarRealizacaoAtividades> atividades = new ArrayList<>();

        try {
            while (rs != null && rs.next()) {
                CriarRealizacaoAtividades cra = buildAtividade(rs);
                Usuario usuarioAdmin = new Usuario();
                usuarioAdmin.setLogin(rs.getString("admin_usuario"));

                Administrador admin = new Administrador();
                admin.setId(rs.getInt("id_admin"));
                admin.setUsuario(usuarioAdmin);

                cra.setAdmin(admin);

                if(cra.getAtv() != null) {
                    cra.getAtv().setDescricao(rs.getString("atividade_descricao"));
                }

                atividades.add(cra);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar atividades: " + e.getMessage());
        }
        return atividades;
    }

    public CriarRealizacaoAtividades getById(Integer id) {
        String sql = "SELECT * FROM criar_realizacao_atividades WHERE id_criacao = " + id;
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

    private String formatBoolean(Boolean value) {
        // Se for null (o que não deve acontecer se o controller for chamado, mas por segurança),
        // definimos como FALSE e adicionamos aspas simples.
        if (value == null || value == false) {
            return "'FALSE'";
        }
        return "'TRUE'";
    }

    // Dentro de CriarRealizacaoAtividadesDAO.java

    // Adicionamos tratamento de nulo, caso o Java envie um Double objeto (embora seja double primitivo)
    private String formatDouble(Double value) {
        if (value == null) {
            return "NULL";
        }
        // Garante que o separador decimal seja o ponto esperado pelo SQL
        return String.valueOf(value);
    }

    public boolean finalizarAtividade(CriarRealizacaoAtividades atividade) {
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

        // --- TEMPORÁRIO PARA DEBUGGING ---
        System.out.println("SQL de FINALIZAÇÃO: " + sql);
        // --- FIM DO TEMPORÁRIO ---

        return SingletonDB.getConexao().manipular(sql);
    }
}