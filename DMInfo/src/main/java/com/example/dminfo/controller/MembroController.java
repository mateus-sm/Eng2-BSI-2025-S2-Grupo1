package com.example.dminfo.controller;

import com.example.dminfo.model.Membro;
import com.example.dminfo.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembroController {

    @Autowired
    private Membro membroModel;

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
        return membroModel.listarMembrosPorAtividade(idCriacao);
    }

    public boolean adicionarMembroAtividade(int idCriacao, int idMembro) {
        return membroModel.adicionarMembroAtividade(idCriacao, idMembro);
    }

    public boolean removerMembroAtividade(int idCriacao, int idMembro) {
        return membroModel.removerMembroAtividade(idCriacao, idMembro);
    }
}