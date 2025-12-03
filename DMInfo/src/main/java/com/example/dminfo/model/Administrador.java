package com.example.dminfo.model;

import com.example.dminfo.dao.AdministradorDAO;
import com.example.dminfo.dao.UsuarioDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
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
    private AdministradorDAO dao = new AdministradorDAO();

    @Autowired
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Administrador() {

    }

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

    public boolean atualizarDtFim(LocalDate novaDtFim, Conexao conexao) {
        if (novaDtFim != null && novaDtFim.isBefore(this.dtIni)) {
            throw new RuntimeException("A data fim não pode ser menor que a data inicial.");
        }

        this.dtFim = novaDtFim;
        return dao.alterar(this, conexao);
    }

    public boolean excluir(Conexao conexao) {
        int totalAdmins = dao.contar(conexao);

        if (totalAdmins <= 1) {
            throw new RuntimeException("Existe apenas um Administrador.");
        }
        return dao.excluir(this.id, conexao);
    }

    public static Administrador buscarPorId(int id, Conexao conexao) {
        return new AdministradorDAO().get(id, conexao);
    }

    public static List<Administrador> listarTodos(Conexao conexao) {
        return new AdministradorDAO().get(conexao);
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