package com.example.dminfo.model;

import java.util.Date;

public class Membro extends Usuario{
    private int idMembro;
    private Date dtIniMembro, dtFimMembro;
    private String observacao;
    private Usuario usuario;

    public Membro(int idMembro, Date dtIniMembro, Date dtFimMembro, String observacao, Usuario usuario) {
        super(usuario.getNome(), usuario.getSenha(), usuario.getUsuario(), usuario.getTelefone(), usuario.getEmail(), usuario.getRua(), usuario.getCidade(), usuario.getBairro(), usuario.getUf(), usuario.getCpf(), usuario.getDtIni(), usuario.getDtFim(), usuario.getDtNasc());
        this.idMembro = idMembro;
        this.dtIniMembro = dtIniMembro;
        this.dtFimMembro = dtFimMembro;
        this.observacao = observacao;

    }

    public int getIdMembro() {
        return idMembro;
    }

    public void setIdMembro(int idMembro) {
        this.idMembro = idMembro;
    }

    public Date getDtIniMembro() {
        return dtIniMembro;
    }

    public void setDtIniMembro(Date dtIniMembro) {
        this.dtIniMembro = dtIniMembro;
    }

    public Date getDtFimMembro() {
        return dtFimMembro;
    }

    public void setDtFimMembro(Date dtFimMembro) {
        this.dtFimMembro = dtFimMembro;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

}
