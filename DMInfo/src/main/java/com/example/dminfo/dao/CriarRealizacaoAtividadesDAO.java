package com.example.dminfo.dao;

import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.example.dminfo.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDate;

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

    @Override
    public CriarRealizacaoAtividades create(CriarRealizacaoAtividades obj, Conexao conexao) {
        return null;
    }

    @Override
    public CriarRealizacaoAtividades read(CriarRealizacaoAtividades obj, Conexao conexao) {
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
    public ResultSet readAll(String filtro, Conexao conexao) {
        return null;
    }

    public ResultSet listarTodas(Conexao conexao) {
        String sql = "SELECT cra.*, " +
                "u.usuario AS admin_usuario, " +
                "atv.descricao AS atividade_descricao " +
                "FROM criar_realizacao_atividades cra " +
                "JOIN administrador adm ON cra.id_admin = adm.id_admin " +
                "JOIN usuario u ON adm.id_usuario = u.id_usuario " +
                "JOIN atividade atv ON cra.id_atividade = atv.id_atividade";

        return conexao.consultar(sql);
    }

    public ResultSet getById(Integer id, Conexao conexao) {
        String sql = "SELECT cra.*, " +
                "u.usuario AS admin_usuario, " +
                "atv.descricao AS atividade_descricao " +
                "FROM criar_realizacao_atividades cra " +
                "JOIN administrador adm ON cra.id_admin = adm.id_admin " +
                "JOIN usuario u ON adm.id_usuario = u.id_usuario " +
                "JOIN atividade atv ON cra.id_atividade = atv.id_atividade " +
                "WHERE cra.id_criacao = " + id;

        return conexao.consultar(sql);
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

    public ResultSet buscarPorData(String data, Conexao conexao) {
        String sql = String.format("SELECT cra.*, " +
                "u.usuario AS admin_usuario, " +
                "atv.descricao AS atividade_descricao " +
                "FROM criar_realizacao_atividades cra " +
                "JOIN administrador adm ON cra.id_admin = adm.id_admin " +
                "JOIN usuario u ON adm.id_usuario = u.id_usuario " +
                "JOIN atividade atv ON cra.id_atividade = atv.id_atividade " +
                "WHERE cra.dtini = '%s'", data);

        return conexao.consultar(sql);
    }

    @Override
    public ResultSet getById(int id, Conexao conexao) {
        return null;
    }
}