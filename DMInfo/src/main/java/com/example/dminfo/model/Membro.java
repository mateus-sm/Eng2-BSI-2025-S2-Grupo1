package com.example.dminfo.model;

import com.example.dminfo.dao.MembroDAO;
import com.example.dminfo.dao.UsuarioDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class Membro {
    private int id;
    private LocalDate dtIni;
    private LocalDate dtFim;
    private String observacao;
    private Usuario usuario;

    @Autowired
    private MembroDAO dao;
    @Autowired
    private UsuarioDAO usuarioDAO;

    public Membro() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDate getDtIni() { return dtIni; }
    public void setDtIni(LocalDate dtIni) { this.dtIni = dtIni; }
    public LocalDate getDtFim() { return dtFim; }
    public void setDtFim(LocalDate dtFim) { this.dtFim = dtFim; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<Membro> listar() {
        return dao.get(""); // Filtro vazio
    }

    public Membro getById(Integer id) {
        return dao.get(id);
    }

    public Membro salvar(Membro membro) {
        if (membro.getUsuario() == null || membro.getUsuario().getId() == 0)
            throw new RuntimeException("ID do Usuário é obrigatório.");

        Usuario usuario = usuarioDAO.get(membro.getUsuario().getId());
        if (usuario == null)
            throw new RuntimeException("Usuário não encontrado para o ID fornecido.");

        if (dao.existsByUsuarioId(usuario.getId()))
            throw new RuntimeException("Este usuário já está associado a um membro.");

        membro.setUsuario(usuario); // Anexa o usuário completo
        membro.setDtIni(LocalDate.now()); // Define a data de início

        return dao.gravar(membro);
    }

    public Membro update(Integer id, Membro membroDetails) {
        Membro membroExistente = dao.get(id);
        if (membroExistente == null)
            throw new RuntimeException("Membro não encontrado com ID: " + id);

        membroExistente.setObservacao(membroDetails.getObservacao());
        membroExistente.setDtFim(membroDetails.getDtFim());

        if (dao.alterar(membroExistente))
            return membroExistente;

        throw new RuntimeException("Erro ao atualizar membro no banco de dados.");
    }

    public boolean excluir(Integer id) {
        Membro membro = dao.get(id);
        if (membro == null) {
            throw new RuntimeException("Membro não encontrado com ID: " + id);
        }
        return dao.excluir(id);
    }
}