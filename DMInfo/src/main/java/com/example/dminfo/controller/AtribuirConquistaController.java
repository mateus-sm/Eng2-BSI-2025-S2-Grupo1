package com.example.dminfo.controller;

import com.example.dminfo.dao.AdministradorDAO;
import com.example.dminfo.dao.AtribuirConquistaMembroDAO;
import com.example.dminfo.dao.MembroDAO;
import com.example.dminfo.model.AtribuirConquistaMembro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Service
public class AtribuirConquistaController {
    @Autowired
    private AtribuirConquistaMembroDAO dao;

    @Autowired
    private MembroDAO membroDAO;

    @Autowired
    private AdministradorDAO administradorDAO;

    public List<AtribuirConquistaMembro> listar() {
        return dao.listar();
    }

    public AtribuirConquistaMembro salvar(AtribuirConquistaMembro acm) {
        if (acm == null || acm.getId() != 0 || acm.getId_admin() != 0
                || acm.getId_membro() != 0 || acm.getId_conquista() != 0
                || acm.getData() != null) {
            throw new RuntimeException("Objeto atribuição inconsistente.");
        }

        AtribuirConquistaMembro existente = dao.consultar(acm.getObservacao());
        if (existente != null) {
            throw new RuntimeException("Atribuição já existe.");
        }

        return dao.gravar(acm);
    }

    public boolean atualizar(AtribuirConquistaMembro acm) {
        if (acm == null || acm.getId() != 0 || acm.getId_admin() != 0
                || acm.getId_membro() != 0 || acm.getId_conquista() != 0
                || acm.getData() != null) {
            throw new RuntimeException("Objeto atribuição inconsistente.");
        }

        return dao.alterar(acm);
    }

    public AtribuirConquistaMembro getById(Integer id) {
        AtribuirConquistaMembro acm = dao.getById(id);
        if (acm == null) {
            throw new RuntimeException("Atribuição não encontrada.");
        }
        return acm;
    }

    public void excluir(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID inválido para exclusão.");
        }
        dao.excluir(id);
    }
}
