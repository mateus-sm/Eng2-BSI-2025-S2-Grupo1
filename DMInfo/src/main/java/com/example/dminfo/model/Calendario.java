package com.example.dminfo.model;

import com.example.dminfo.dao.CalendarioDAO;
import com.example.dminfo.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.mail.javamail.JavaMailSender;

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

    private List<Usuario> observadores = new ArrayList<>();

    public void adicionarObservador(Usuario u) {
        this.observadores.add(u);
    }
    public void removerObservador(Usuario u) {
        this.observadores.remove(u);
    }

    public void carregarEInstanciarObservadores(Conexao conexao) {
        this.observadores.clear(); // Limpa a lista antes de carregar

        if (this.id_criacao == null || conexao == null) return;

        String sql = String.format(
                "SELECT u.nome, u.email " +
                        "FROM criar_realizacao_atividades_membro cram " +
                        "JOIN membro m ON cram.membro_id_membro = m.id_membro " +
                        "JOIN usuario u ON m.id_usuario = u.id_usuario " +
                        "WHERE cram.criar_realizacao_atividades_id_criacao = %d", this.id_criacao.getId());

        try {
            ResultSet rs = conexao.consultar(sql);
            while (rs != null && rs.next()) {
                // Instanciando o usuário dentro do calendário conforme pedido
                Usuario observador = new Usuario();
                observador.setNome(rs.getString("nome"));
                observador.setEmail(rs.getString("email"));

                // Adicionando o usuário à lista de observadores
                this.adicionarObservador(observador);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao instanciar observadores no calendário: " + e.getMessage());
        }
    }

    public void notificarObservadores(String motivo, JavaMailSender mailSender) {
        if (this.observadores.isEmpty()) {
            System.out.println("O calendário da atividade não possui observadores para notificar.");
            return;
        }

        for (Usuario observador : this.observadores) {
            // Delega para o observador (usuário) a ação de receber a notificação
            observador.receberNotificacao(this.id_criacao, motivo, mailSender);
        }
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