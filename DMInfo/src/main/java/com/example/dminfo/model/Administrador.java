package com.example.dminfo.model;

import com.example.dminfo.dao.AdministradorDAO;
import com.example.dminfo.dao.UsuarioDAO;
import com.example.dminfo.util.Conexao;
import com.example.dminfo.util.SingletonDB;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class Administrador {

    private int id;
    private LocalDate dtIni;
    private LocalDate dtFim;
    private Usuario usuario;

    @Autowired
    @JsonIgnore
    private AdministradorDAO dao;

    @Autowired
    @JsonIgnore
    private UsuarioDAO usuarioDAO;

    public Administrador() { }

    public Administrador(LocalDate dtIni, LocalDate dtFim, Usuario usuario) {
        this.dtIni = dtIni;
        this.dtFim = dtFim;
        this.usuario = usuario;
    }

    private Administrador montarAdministrador(ResultSet rs) throws SQLException {
        Administrador admin = new Administrador();
        admin.setId(rs.getInt("id_admin"));

        if (rs.getDate("dtini") != null)
            admin.setDtIni(rs.getDate("dtini").toLocalDate());

        if (rs.getDate("dtfim") != null)
            admin.setDtFim(rs.getDate("dtfim").toLocalDate());

        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id_usuario"));
        String nome = null;
        try { nome = rs.getString("usuario_nome"); } catch (SQLException ignore) {}
        usuario.setNome(nome);

        admin.setUsuario(usuario);
        return admin;
    }

    public List<Administrador> listarTodos(Conexao conexao) {
        List<Administrador> lista = new ArrayList<>();
        ResultSet rs = dao.get(conexao);
        try {
            if (rs != null) {
                while (rs.next()) {
                    lista.add(montarAdministrador(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar administradores: " + e.getMessage());
        }
        return lista;
    }

    public Administrador getById(int id, Conexao conexao) {
        if (id <= 0) return null;

        ResultSet rs = dao.get(id, conexao);
        try {
            if (rs != null && rs.next()) {
                return montarAdministrador(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar administrador por ID: " + e.getMessage());
        }
        return null;
    }

    public Administrador getByUsuario(int idUsuario, Conexao conexao) {
        ResultSet rs = dao.getByUsuario(idUsuario, conexao);
        try {
            if (rs != null && rs.next()) {
                return montarAdministrador(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar administrador por usuário: " + e.getMessage());
        }
        return null;
    }

    public List<Administrador> filtrar(String nome, String dtIni, String dtFim, Conexao conexao) {
        List<Administrador> lista = new ArrayList<>();
        ResultSet rs = dao.filtrar(nome, dtIni, dtFim, conexao);
        try {
            if (rs != null) {
                while (rs.next()) {
                    lista.add(montarAdministrador(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao filtrar administradores: " + e.getMessage());
        }
        return lista;
    }

    public Administrador salvar(Conexao conexao) {
        if (usuarioDAO.get(this.usuario.getId()) == null) {
            throw new RuntimeException("Usuário não existente");
        }

        if (this.getByUsuario(this.usuario.getId(), conexao) != null) {
            throw new RuntimeException("Usuário já é um Administrador");
        }

        return dao.gravar(this, conexao);
    }

    public Administrador atualizarDtFim(int id, LocalDate novaDtFim, Conexao conexao) {
        Administrador adminBanco = this.getById(id, conexao);

        if (adminBanco == null) {
            throw new RuntimeException("Administrador não encontrado.");
        }

        if (novaDtFim != null && adminBanco.getDtIni() != null && novaDtFim.isBefore(adminBanco.getDtIni())) {
            throw new RuntimeException("A data fim não pode ser menor que a data inicial.");
        }

        adminBanco.setDtFim(novaDtFim);

        if (dao.alterar(adminBanco, conexao)) {
            return adminBanco;
        }
        throw new RuntimeException("Erro ao atualizar data fim.");
    }

    public boolean excluir(int id, Conexao conexao) {
        if (id <= 0) {
            throw new RuntimeException("ID inválido.");
        }
        if (dao.contar(conexao) <= 1) {
            throw new RuntimeException("Não pode deixar de existir Administrador.");
        }
        return dao.excluir(id, conexao);
    }

    public int contar(Conexao conexao) {
        return dao.contar(conexao);
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public LocalDate getDtIni() {return dtIni;}
    public void setDtIni(LocalDate dtIni) {this.dtIni = dtIni;}
    public LocalDate getDtFim() {return dtFim;}
    public void setDtFim(LocalDate dtFim) {this.dtFim = dtFim;}
    public Usuario getUsuario() {return usuario;}
    public void setUsuario(Usuario usuario) {this.usuario = usuario;}
}