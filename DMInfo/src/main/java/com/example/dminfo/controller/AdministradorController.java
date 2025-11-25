package com.example.dminfo.controller;

import com.example.dminfo.dao.AdministradorDAO;
import com.example.dminfo.model.Administrador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdministradorController {

    @Autowired
    private AdministradorDAO dao;

    public Administrador buscar(int id) {
        return dao.get(id);
    }

    public List<Administrador> listar() {
        return dao.get("");
    }

    public Administrador salvar(Administrador administrador) {
        return dao.gravar(administrador);
    }

    public Administrador update(int id, Administrador adminDetails) {
        Administrador existente = dao.get(id);

        if (existente == null) return null;

        existente.setDtFim(adminDetails.getDtFim());
        dao.alterar(existente);

        return existente;
    }

    public boolean excluir(int id) {
        return dao.excluir(id);
    }
}
