package com.example.dminfo.controller;

import com.example.dminfo.model.Administrador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministradorController {

    @Autowired
    private Administrador adminModel; // Injeta o "Fat Model"

    public Administrador getById(Integer id) {
        return adminModel.getById(id);
    }

    public List<Administrador> listar() {
        return adminModel.listar();
    }

    public Administrador salvar(Administrador administrador) {
        return adminModel.salvar(administrador);
    }

    public Administrador update(Integer id, Administrador adminDetails) {
        return adminModel.update(id, adminDetails);
    }

    public boolean excluir(Integer id) {
        return adminModel.excluir(id);
    }
}