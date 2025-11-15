package com.example.dminfo.controller;

import com.example.dminfo.model.Administrador;
import com.example.dminfo.model.Atividade;
import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.example.dminfo.model.Usuario;
import com.example.dminfo.util.Conexao;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarioController {

    private Conexao conexao;

    private CriarRealizacaoAtividades buildAtividade(ResultSet rs) throws SQLException {
        Administrador admin = new Administrador();
        admin.setId(rs.getInt("id_admin"));

        Atividade atividade = new Atividade();
        atividade.setId(rs.getInt("id_atividade"));

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

    public List<CriarRealizacaoAtividades> listarTodasAtividades() {
        this.conexao = SingletonDB.getConexao();

        String sql = "SELECT cra.*, " +
                "u.usuario AS admin_usuario, " +
                "atv.descricao AS atividade_descricao, " +
                "c.id_criacao AS id_calendario_ativo " +
                "FROM criar_realizacao_atividades cra " +
                "JOIN administrador adm ON cra.id_admin = adm.id_admin " +
                "JOIN usuario u ON adm.id_usuario = u.id_usuario " +
                "JOIN atividade atv ON cra.id_atividade = atv.id_atividade " +
                "LEFT JOIN calendario c ON cra.id_criacao = c.id_criacao";

        ResultSet rs = this.conexao.consultar(sql);
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
            System.err.println("Erro ao listar atividades para calendário: " + e.getMessage());
        }
        return atividades;
    }

    public List<Integer> listarAtividadesAtivasIds() {
        this.conexao = SingletonDB.getConexao();

        String sql = "SELECT id_criacao FROM calendario";
        ResultSet rs = this.conexao.consultar(sql);
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

    public boolean adicionarAtividadeAoCalendario(int idCriacao) {
        this.conexao = SingletonDB.getConexao();

        String sqlCheck = "SELECT COUNT(*) FROM calendario WHERE id_criacao = " + idCriacao;
        try (ResultSet rs = this.conexao.consultar(sqlCheck)) {
            if (rs != null && rs.next() && rs.getInt(1) > 0) {
                return true;
            }

            String sqlInsert = String.format("INSERT INTO calendario (id_criacao) VALUES (%d)", idCriacao);

            System.out.println("DEBUG SQL INSERT: " + sqlInsert);

            return this.conexao.manipular(sqlInsert);

        } catch (SQLException e) {
            System.err.println("Erro SQL/ResultSet no CalendarioController: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Erro inesperado no CalendarioController ao adicionar: " + e.getMessage());
            return false;
        }
    }

    public boolean removerAtividadeDoCalendario(int idCriacao) {
        this.conexao = SingletonDB.getConexao();
        if (this.conexao == null || !this.conexao.getEstadoConexao()) {
            System.err.println("ERRO CRÍTICO: Conexão inválida ou nula. Detalhe: " + (this.conexao != null ? this.conexao.getMensagemErro() : "Objeto nulo."));
            return false;
        }

        String sql = "DELETE FROM calendario WHERE id_criacao = " + idCriacao;

        return this.conexao.manipular(sql);
    }
}