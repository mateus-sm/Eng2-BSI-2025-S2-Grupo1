package com.example.dminfo.model;

import com.example.dminfo.dao.AdministradorDAO;
import com.example.dminfo.dao.UsuarioDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class Administrador {

    // Campos do Modelo (POJO)
    private int id;
    private LocalDate dtIni;
    private LocalDate dtFim;
    private Usuario usuario;

    // Injeção dos DAOs
    @Autowired
    private AdministradorDAO dao;
    @Autowired
    private UsuarioDAO usuarioDAO;

    // Construtor
    public Administrador() {}

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDate getDtIni() { return dtIni; }
    public void setDtIni(LocalDate dtIni) { this.dtIni = dtIni; }
    public LocalDate getDtFim() { return dtFim; }
    public void setDtFim(LocalDate dtFim) { this.dtFim = dtFim; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    // --- LÓGICA DE NEGÓCIOS (do antigo AdministradorService) ---

    public Administrador getById(Integer id) {
        return dao.get(id);
    }

    public List<Administrador> listar() {
        return dao.get(""); // Filtro vazio
    }

    public Administrador salvar(Administrador administrador) {
        // Busca o usuário pelo ID fornecido no JSON
        Usuario usuario = usuarioDAO.get(administrador.getUsuario().getId());
        if (usuario == null)
            throw new RuntimeException("Usuário não encontrado para o ID fornecido.");

        // Verifica se o usuário já é um administrador
        Administrador administradorExistente = dao.getByUsuario(usuario.getId());
        if (administradorExistente != null) {
            throw new RuntimeException("Este usuário já está associado a um administrador.");
        }

        // Define os dados do novo admin
        administrador.setUsuario(usuario);
        administrador.setDtIni(LocalDate.now());
        return dao.gravar(administrador);
    }

    // Este 'update' segue a lógica do seu service (só seta dtFim)
    public Administrador update(Integer id, Administrador adminDetails) {
        Administrador adminBanco = dao.get(id);
        if (adminBanco == null) {
            throw new RuntimeException("Administrador não encontrado para o ID: " + id);
        }
        adminBanco.setDtFim(adminDetails.getDtFim());

        if (dao.alterar(adminBanco)) {
            return adminBanco;
        }
        throw new RuntimeException("Erro ao atualizar administrador (setar dtFim).");
    }

    // Este 'excluir' segue a lógica do seu service (hard delete)
    public boolean excluir(Integer id) {
        Administrador administrador = dao.get(id);
        if (administrador == null) {
            throw new RuntimeException("Administrador não encontrado.");
        }
        return dao.excluir(id);
    }
}