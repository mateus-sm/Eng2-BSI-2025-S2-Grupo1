package com.example.dminfo.model;

import com.example.dminfo.dao.CalendarioDAO;
import com.example.dminfo.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class Calendario {
    private int id_calendario;
    private CriarRealizacaoAtividades id_criacao;

    @Autowired
    private CalendarioDAO dao;

    public Calendario() {
    }

    public Calendario(CriarRealizacaoAtividades id_criacao) {
        this.id_criacao = id_criacao;
    }

    private CriarRealizacaoAtividades montarAtividade(ResultSet rs) throws SQLException {
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

    public List<CriarRealizacaoAtividades> listarTodasAtividades(Conexao conexao) {
        List<CriarRealizacaoAtividades> lista = new ArrayList<>();
        ResultSet rs = dao.listarTodasAtividades(conexao);
        try {
            if (rs != null) {
                while (rs.next()) {
                    lista.add(montarAtividade(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar atividades do calendário: " + e.getMessage());
        }
        return lista;
    }

    public List<Integer> listarAtividadesAtivasIds(Conexao conexao) {
        List<Integer> ids = new ArrayList<>();
        ResultSet rs = dao.listarAtividadesAtivasIds(conexao);
        try {
            if (rs != null) {
                while (rs.next()) {
                    ids.add(rs.getInt("id_criacao"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar IDs ativos: " + e.getMessage());
        }
        return ids;
    }

    public Calendario salvar(Calendario calendario, Conexao conexao) {
        if (calendario == null || calendario.getId_criacao() == null) {
            throw new RuntimeException("Dados inválidos para adicionar ao calendário.");
        }
        return dao.create(calendario, conexao);
    }

    public boolean excluir(Integer idCriacao, Conexao conexao) {
        if (idCriacao == null) {
            throw new RuntimeException("ID inválido para exclusão.");
        }
        return dao.delete(idCriacao, conexao);
    }

    public int getId_calendario() {
        return id_calendario;
    }
    public void setId_calendario(int id_calendario) {
        this.id_calendario = id_calendario;
    }
    public CriarRealizacaoAtividades getId_criacao() {
        return id_criacao;
    }
    public void setId_criacao(CriarRealizacaoAtividades id_criacao) {
        this.id_criacao = id_criacao;
    }
}