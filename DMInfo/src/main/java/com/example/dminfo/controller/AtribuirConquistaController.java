package com.example.dminfo.controller;

import com.example.dminfo.dao.AdministradorDAO;
import com.example.dminfo.dao.MembroDAO;
import com.example.dminfo.model.AtribuirConquistaMembro;
import com.example.dminfo.model.Conquista;
import com.example.dminfo.util.Conexao;
import com.example.dminfo.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Service
public class AtribuirConquistaController {
    @Autowired
    private AtribuirConquistaMembro acmModel;

    @Autowired
    private MembroDAO membroDAO;

    @Autowired
    private AdministradorDAO administradorDAO;

    @Autowired
    private Conquista conquistaModel;

    public List<AtribuirConquistaMembro> listar() {
        return acmModel.listar("", SingletonDB.getConexao());
    }

    public AtribuirConquistaMembro salvar(AtribuirConquistaMembro acm) {
        Conexao conexao = SingletonDB.getConexao();
        if (acm == null || acm.getId() != 0)
            throw new RuntimeException("Atribuição inválida para criação.");

        if (administradorDAO.get(acm.getId_admin()) == null ||
                membroDAO.get(acm.getId_membro()) == null ||
                conquistaModel.getById(acm.getId_conquista(), SingletonDB.getConexao()) == null)
            throw new RuntimeException("Administrador, membro ou conquista não encontrados.");

        if (acmModel.getById(acm.getId(), conexao) != null)
            throw new RuntimeException("Atribuição já existe.");

        return acmModel.salvar(acm, conexao);
    }

    public AtribuirConquistaMembro atualizar(AtribuirConquistaMembro acm) {
        if (acm == null || acm.getId() == 0)
            throw new RuntimeException("Atribuição inválida para atualização.");

        if (administradorDAO.get(acm.getId_admin()) == null ||
                membroDAO.get(acm.getId_membro()) == null  ||
                conquistaModel.getById(acm.getId_conquista(), SingletonDB.getConexao()) == null)
            throw new RuntimeException("Administrador, membro ou conquista não encontrados.");

        acmModel.alterar(acm, SingletonDB.getConexao());
        return acm;
    }

    public AtribuirConquistaMembro getById(Integer id) {
        AtribuirConquistaMembro acm = acmModel.getById(id, SingletonDB.getConexao());
        if (acm == null) {
            throw new RuntimeException("Atribuição não encontrada.");
        }
        return acm;
    }

    public void excluir(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID inválido para exclusão.");
        }
        acmModel.excluir(id,  SingletonDB.getConexao());
    }
}
