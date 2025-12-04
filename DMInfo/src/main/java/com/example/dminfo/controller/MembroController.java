package com.example.dminfo.controller;

import com.example.dminfo.dao.MembroAtividadeDAO;
import com.example.dminfo.model.Membro;
import com.example.dminfo.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembroController {

    @Autowired
    private Membro membroModel;

    @Autowired(required = false)
    private MembroAtividadeDAO membroAtividadeDAO;

    public List<Membro> listar(String filtro) {
        return membroModel.listar(filtro, SingletonDB.getConexao());
    }

    public Membro getById(Integer id) {
        Membro membro = membroModel.getById(id, SingletonDB.getConexao());
        if (membro == null)
            throw new RuntimeException("Membro não encontrado.");
        return membro;
    }

    public Membro salvar(Membro membro) {
        if (membro == null || membro.getUsuario() == null)
            throw new RuntimeException("Dados do membro incompletos.");

        membroModel.setUsuario(membro.getUsuario());
        membroModel.setDtIni(membro.getDtIni());
        membroModel.setDtFim(membro.getDtFim());
        membroModel.setObservacao(membro.getObservacao());

        return membroModel.salvar(SingletonDB.getConexao());
    }

    public Membro update(Integer id, Membro membroDetails) {
        return membroModel.atualizar(id, membroDetails, SingletonDB.getConexao());
    }

    public boolean excluir(Integer id) {
        return membroModel.excluir(id, SingletonDB.getConexao());
    }

    public List<Integer> listarMembrosPorAtividade(int idCriacao) {
        if (membroAtividadeDAO != null)
            return membroAtividadeDAO.listarMembrosPorAtividade(idCriacao);
        return List.of();
    }

    public boolean adicionarMembroAtividade(int idCriacao, int idMembro) {
        if (membroAtividadeDAO != null)
            return membroAtividadeDAO.adicionarMembroAtividade(idCriacao, idMembro);
        return false;
    }

    public boolean removerMembroAtividade(int idCriacao, int idMembro) {
        if (membroAtividadeDAO != null)
            return membroAtividadeDAO.removerMembroAtividade(idCriacao, idMembro);
        return false;
    }
}