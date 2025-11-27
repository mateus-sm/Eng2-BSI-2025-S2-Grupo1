package com.example.dminfo.controller;

import com.example.dminfo.dao.MembroAtividadeDAO;
import com.example.dminfo.dao.MembroDAO;
import com.example.dminfo.dao.UsuarioDAO;
import com.example.dminfo.model.Membro;
import com.example.dminfo.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembroController {

    @Autowired
    private Membro membroModel;

    @Autowired
    private MembroDAO membroDAO;

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private MembroAtividadeDAO membroAtividadeDAO;

    public List<Membro> listar(String filtro) {
        return membroModel.listar(filtro);
    }

    public Membro getById(Integer id) {
        Membro membro = membroModel.getById(id);
        if (membro == null)
            throw new RuntimeException("Membro não encontrado.");
        return membro;
    }

    public Membro salvar(Membro membro) {
        if (membro == null)
            throw new RuntimeException("Dados inválidos para criação.");

        if (membro.getUsuario() == null || membro.getUsuario().getId() == 0)
            throw new RuntimeException("ID do Usuário é obrigatório.");

        Usuario usuario = usuarioDAO.get(membro.getUsuario().getId());
        if (usuario == null)
            throw new RuntimeException("Usuário não encontrado para o ID fornecido.");

        if (membroDAO.existsByUsuarioId(usuario.getId()))
            throw new RuntimeException("Este usuário já está associado a um membro.");

        membro.setUsuario(usuario);

        return membroModel.salvar(membro);
    }

    public Membro update(Integer id, Membro membroDetails) {
        if (id == null || membroDetails == null)
            throw new RuntimeException("Dados inválidos para atualização.");

        Membro membroExistente = membroModel.getById(id);
        if (membroExistente == null)
            throw new RuntimeException("Membro não encontrado com ID: " + id);

        membroExistente.setObservacao(membroDetails.getObservacao());
        membroExistente.setDtFim(membroDetails.getDtFim());

        return membroModel.alterar(membroExistente);
    }

    public boolean excluir(Integer id) {
        if (id == null)
            throw new RuntimeException("ID inválido para exclusão.");
        Membro membro = membroModel.getById(id);
        if (membro == null)
            throw new RuntimeException("Membro não encontrado para exclusão.");
        return membroModel.excluir(id);
    }

    public List<Integer> listarMembrosPorAtividade(int idCriacao) {
        return membroAtividadeDAO.listarMembrosPorAtividade(idCriacao);
    }

    public boolean adicionarMembroAtividade(int idCriacao, int idMembro) {
        return membroAtividadeDAO.adicionarMembroAtividade(idCriacao, idMembro);
    }

    public boolean removerMembroAtividade(int idCriacao, int idMembro) {
        return membroAtividadeDAO.removerMembroAtividade(idCriacao, idMembro);
    }
}