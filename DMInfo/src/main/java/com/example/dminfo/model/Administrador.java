package com.example.dminfo.model;

import com.example.dminfo.dao.AdministradorDAO;
import com.example.dminfo.dao.UsuarioDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository; // ou @Service
import com.example.dminfo.util.Conexao;

import java.time.LocalDate;
import java.util.List;

@Repository
public class Administrador {

    private int id;
    private LocalDate dtIni;
    private LocalDate dtFim;
    private Usuario usuario;

    @Autowired
    private AdministradorDAO dao;

    @Autowired
    private UsuarioDAO usuarioDAO;

    public Administrador() {}

    public Administrador(LocalDate dtIni, LocalDate dtFim, Usuario usuario) {
        this.dtIni = dtIni;
        this.dtFim = dtFim;
        this.usuario = usuario;
    }

    public Administrador salvar(Conexao conexao) {
        if (usuarioDAO.get(this.usuario.getId()) == null) {
            throw new RuntimeException("Usuário não existente");
        }
        if (dao.getByUsuario(this.usuario.getId(), conexao) != null) {
            throw new RuntimeException("Usuário já é um Administrador");
        }
        return dao.gravar(this, conexao);
    }

    public Administrador atualizarDtFim(int id, LocalDate novaDtFim, Conexao conexao) {
        Administrador adminBanco = dao.get(id, conexao);
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
        int totalAdmins = dao.contar(conexao);
        if (totalAdmins <= 1) {
            throw new RuntimeException("Existe apenas um Administrador. Não é possível excluir o último.");
        }
        return dao.excluir(id, conexao);
    }

    public Administrador getByUsuario(int idUsuario, Conexao conexao) {
        return dao.getByUsuario(idUsuario, conexao);
    }

    public Administrador getById(int id, Conexao conexao) {
        return dao.get(id, conexao);
    }

    public List<Administrador> listarTodos(Conexao conexao) {
        return dao.get(conexao);
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