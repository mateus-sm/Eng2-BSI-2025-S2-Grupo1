package com.example.dminfo.controller;

import com.example.dminfo.model.Administrador;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdministradorController {

    public Administrador buscar(int id) {
        return Administrador.buscarPorId(id);
    }

    public List<Administrador> listar() {
        return Administrador.listarTodos();
    }

    public Administrador salvar(Administrador administrador) {
        return administrador.salvar();
    }

    public Administrador update(int id, Administrador adminDetails) {
        Administrador existente = Administrador.buscarPorId(id);

        if (existente == null) return null;

        existente.atualizarDtFim(adminDetails.getDtFim());

        return existente;
    }

    public boolean excluir(int id) {
        Administrador admin = new Administrador();
        admin.setId(id);
        return admin.excluir();
    }
}