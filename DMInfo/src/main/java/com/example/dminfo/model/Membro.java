package com.example.dminfo.model;

import com.example.dminfo.dao.MembroDAO;
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

    public List<Membro> listar(String filtro) {
        return dao.get(filtro);
    }

    public Membro getById(Integer id) {
        return dao.get(id);
    }

    public Membro salvar(Membro membro) {
        if (membro.getDtIni() == null)
            membro.setDtIni(LocalDate.now());
        return dao.gravar(membro);
    }

    public Membro alterar(Membro membro) {
        if (dao.alterar(membro))
            return membro;
        throw new RuntimeException("Erro ao atualizar membro no banco de dados.");
    }

    public boolean excluir(Integer id) {
        return dao.excluir(id);
    }
}