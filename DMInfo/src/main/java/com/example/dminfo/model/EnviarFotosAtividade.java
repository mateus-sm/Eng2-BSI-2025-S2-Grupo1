package com.example.dminfo.model;

import com.example.dminfo.dao.AtividadeDAO;
import com.example.dminfo.dao.EnviarFotosAtividadeDAO;
import com.example.dminfo.dao.MembroDAO;
import com.example.dminfo.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class EnviarFotosAtividade {

    private int id;
    private Membro membro;
    private Atividade atividade;
    private String foto;
    private LocalDate data;

    @Autowired
    private EnviarFotosAtividadeDAO dao;

    @Autowired
    private MembroDAO membroDAO;

    @Autowired
    private AtividadeDAO atividadeDAO;

    public EnviarFotosAtividade(){}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Membro getMembro() { return membro; }
    public void setMembro(Membro membro) { this.membro = membro; }
    public Atividade getAtividade() { return atividade; }
    public void setAtividade(Atividade atividade) { this.atividade = atividade; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public EnviarFotosAtividade gravar(EnviarFotosAtividade fotoObj, Conexao conexao) {
        Membro m = membroDAO.getByUsuario(fotoObj.getMembro().getUsuario().getId(), conexao);
        if (m == null)
            throw new RuntimeException("Membro não encontrado para o usuário informado.");
        fotoObj.setMembro(m);

        Atividade a = atividadeDAO.get(fotoObj.getAtividade().getId(), conexao);
        if (a == null)
            throw new RuntimeException("Atividade não encontrada.");
        fotoObj.setAtividade(a);

        if (fotoObj.getData() == null)
            fotoObj.setData(LocalDate.now());

        return dao.gravar(fotoObj, conexao);
    }

    public EnviarFotosAtividade getById(int idFoto, Conexao conexao) {
        return dao.get(idFoto, conexao);
    }

    public List<EnviarFotosAtividade> listarPorAtividade(int idAtividade, Conexao conexao) {
        return dao.getPorAtividade(idAtividade, conexao);
    }

    public boolean excluir(int idFoto, Conexao conexao) {
        if (idFoto <= 0)
            throw new RuntimeException("ID de foto inválido.");
        return dao.excluir(idFoto, conexao);
    }
}