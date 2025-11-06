package com.example.dminfo.model;

import com.example.dminfo.dao.AtividadeDAO;
import com.example.dminfo.dao.EnviarFotosAtividadeDAO;
import com.example.dminfo.dao.MembroDAO;
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

    @Autowired(required = false)
    private EnviarFotosAtividadeDAO dao;
    @Autowired(required = false)
    private MembroDAO membroDAO;
    @Autowired(required = false)
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

    public List<EnviarFotosAtividade> listarPorAtividade(int idAtividade) {
        return dao.getPorAtividade(idAtividade);
    }

    public EnviarFotosAtividade salvar(EnviarFotosAtividade foto) {

        Membro m = membroDAO.get(foto.getMembro().getId());
        if (m == null)
            throw new RuntimeException("Membro não encontrado com ID: " + foto.getMembro().getId());

        Atividade a = atividadeDAO.getById(foto.getAtividade().getId());
        if (a == null)
            throw new RuntimeException("Atividade não encontrada com ID: " + foto.getAtividade().getId());

        if (foto.getFoto() == null || foto.getFoto().isEmpty())
            throw new RuntimeException("O caminho da foto (URL) é obrigatório.");

        // Regras de negócio
        foto.setData(LocalDate.now());
        foto.setMembro(m);
        foto.setAtividade(a);

        return dao.gravar(foto);
    }
}