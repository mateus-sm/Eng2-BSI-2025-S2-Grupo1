package com.example.dminfo.controller;

import com.example.dminfo.model.Administrador;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministradorController {

    public Administrador buscar(int id) {
        return Administrador.buscarPorId(id, SingletonDB.getConexao());
    }

    public List<Administrador> listar() {
        return Administrador.listarTodos(SingletonDB.getConexao());
    }

    public Administrador salvar(Administrador administrador) {
        return administrador.salvar(SingletonDB.getConexao());
    }

    public Administrador update(int id, Administrador adminDetails) {
        Administrador existente = Administrador.buscarPorId(id, SingletonDB.getConexao());

        if (existente == null) return null;

        existente.atualizarDtFim(adminDetails.getDtFim(),SingletonDB.getConexao());

        return existente;
    }

    public boolean excluir(int id) {
        Administrador admin = new Administrador();
        admin.setId(id);
        return admin.excluir(SingletonDB.getConexao());
    }
}