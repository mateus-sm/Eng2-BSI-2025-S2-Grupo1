package com.example.dminfo.model;

import com.example.dminfo.dao.MembroDAO;
import com.example.dminfo.dao.UsuarioDAO;
import com.example.dminfo.util.Conexao;
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

    public List<Membro> listar(String filtro, Conexao conexao) {
        return dao.get(filtro, conexao);
    }

    public Membro getById(Integer id, Conexao conexao) {
        if (id == null || id == 0)
            throw new RuntimeException("ID inválido.");
        return dao.get(id, conexao);
    }

    public Membro salvar(Conexao conexao) {
        if (this.usuario == null || this.usuario.getId() == 0)
            throw new RuntimeException("Usuário inválido ou não informado.");

        if (dao.getByUsuario(this.usuario.getId(), conexao) != null)
            throw new RuntimeException("Usuário já é um Membro.");

        if (this.dtIni == null)
            this.dtIni = LocalDate.now();

        if (this.observacao == null)
            this.observacao = "";

        return dao.gravar(this, conexao);
    }

    public Membro atualizar(int id, Membro membroDetails, Conexao conexao) {
        Membro membroBanco = dao.get(id, conexao);
        if (membroBanco == null)
            throw new RuntimeException("Membro não encontrado para atualização.");

        if (membroDetails == null)
            throw new RuntimeException("Dados inválidos.");

        membroBanco.setObservacao(membroDetails.getObservacao());
        membroBanco.setDtFim(membroDetails.getDtFim());

        if (dao.alterar(membroBanco, conexao))
            return membroBanco;
        throw new RuntimeException("Erro ao atualizar dados do membro.");
    }

    public boolean excluir(Integer id, Conexao conexao) {
        if (id == null || id <= 0)
            throw new RuntimeException("ID inválido.");
        return dao.excluir(id, conexao);
    }
}