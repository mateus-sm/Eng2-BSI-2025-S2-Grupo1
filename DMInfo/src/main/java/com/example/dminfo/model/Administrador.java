package com.example.dminfo.model;

import com.example.dminfo.dao.AdministradorDAO;
import com.example.dminfo.dao.UsuarioDAO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.List;

public class Administrador {

    private int id;
    private LocalDate dtIni;
    private LocalDate dtFim;
    private Usuario usuario;

    @JsonIgnore
    private AdministradorDAO dao = new AdministradorDAO();

    @JsonIgnore
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Administrador() {}

    public Administrador salvar() {
        if (usuarioDAO.get(this.usuario.getId()) == null) {
            throw new RuntimeException("Usuário não existente");
        }

        if (dao.getByUsuario(this.usuario.getId()) != null) {
            throw new RuntimeException("Usuário já é um Administrador");
        }

        return dao.gravar(this);
    }

    public boolean atualizarDtFim(LocalDate novaDtFim) {
        if (novaDtFim != null && novaDtFim.isBefore(this.dtIni)) {
            throw new RuntimeException("A data fim não pode ser menor que a data inicial.");
        }

        this.dtFim = novaDtFim;
        return dao.alterar(this);
    }

    public boolean excluir() {
        return dao.excluir(this.id);
    }

    public static Administrador buscarPorId(int id) {
        return new AdministradorDAO().get(id);
    }

    public static List<Administrador> listarTodos() {
        return new AdministradorDAO().get();
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