package com.example.dminfo.model;

import com.example.dminfo.dao.CalendarioDAO;
import com.example.dminfo.dao.MembroAtividadeDAO;
import com.example.dminfo.dao.MembroDAO;
import com.example.dminfo.model.observer.ObserverMembro;
import com.example.dminfo.model.observer.SujeitoCalendario;
import com.example.dminfo.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.mail.javamail.JavaMailSender;

@Repository
public class Calendario implements SujeitoCalendario {
    private int id_calendario;
    private CriarRealizacaoAtividades id_criacao;
    private List<ObserverMembro> observadores = new ArrayList<>();

    @Autowired
    private CalendarioDAO dao;

    @Autowired
    private MembroAtividadeDAO membroAtividadeDAO;

    @Autowired
    private MembroDAO membroDAO;

    @Autowired
    private Membro membro;

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

    @Override
    public void adicionarObservador(ObserverMembro observador) {
        this.observadores.add(observador);
    }

    @Override
    public void removerObservador(ObserverMembro observador) {
        this.observadores.remove(observador);
    }

    @Override
    public void notificarObservadores(String motivo, JavaMailSender mailSender) {
        for (ObserverMembro obs : observadores) {
            obs.notificarMembros(motivo, mailSender);
        }
    }

    public void carregarEInstanciarObservadores(Conexao conexao) {
        this.observadores.clear();

        if (this.id_criacao != null && this.id_criacao.getId() > 0) {
            try {
                List<Integer> idsMembros = membroAtividadeDAO.listarMembrosPorAtividade(this.id_criacao.getId());
                if (idsMembros != null && !idsMembros.isEmpty()) {
                    for (Integer idMembro : idsMembros) {
                        Membro membro = membroDAO.get(idMembro, conexao);
                        if (membro != null) {
                            this.adicionarObservador(membro);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro ao carregar observadores da atividade: " + e.getMessage());
            }
        } else {
            System.err.println("Aviso: Calendário sem ID de criação. Não foi possível carregar observadores.");
        }
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