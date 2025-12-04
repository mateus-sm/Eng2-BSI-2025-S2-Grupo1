package com.example.dminfo.model;

import com.example.dminfo.dao.AdministradorDAO;
import com.example.dminfo.dao.UsuarioDAO;
import com.example.dminfo.util.SingletonDB;
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

    public Administrador atualizarDtFim(int id, Administrador adminDetails, Conexao conexao) {
        Administrador adminBanco = dao.get(id, conexao);
        if (adminBanco == null) {
            throw new RuntimeException("Administrador não encontrado.");
        }

        if (adminDetails == null) {
            throw new RuntimeException("Dados inválidos para atualização.");
        }

        if (adminDetails.getDtFim() != null && adminBanco.getDtIni() != null && adminDetails.getDtFim().isBefore(adminBanco.getDtIni())) {
            throw new RuntimeException("A data fim não pode ser menor que a data inicial.");
        }

        adminBanco.setDtFim(adminDetails.getDtFim());

        if (dao.alterar(adminBanco, conexao)) {
            return adminBanco;
        }
        throw new RuntimeException("Erro ao atualizar data fim.");
    }

    public boolean excluir(int id, Conexao conexao) {
        if (id <= 0) {
            throw new RuntimeException("ID inválido.");
        }
        if (dao.contar(SingletonDB.getConexao()) == 1) {
            throw new RuntimeException("Não pode deixar de existir Administrador.");
        }
        return dao.excluir(id, conexao);
    }

    public int contar(Conexao conexao) {
        return dao.contar(conexao);
    }

    public Administrador getByUsuario(int idUsuario, Conexao conexao) {
        return dao.getByUsuario(idUsuario, conexao);
    }

    public Administrador getById(int id, Conexao conexao) {
        if (id == 0) {
            throw new RuntimeException("Administrador inválido.");
        }
        return dao.get(id, conexao);
    }

    public List<Administrador> filtrar(String nome, String dtIni, String dtFim, Conexao conexao) {
        return dao.filtrar(nome, dtIni, dtFim, conexao);
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