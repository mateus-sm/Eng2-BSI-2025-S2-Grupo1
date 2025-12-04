package com.example.dminfo.controller;

import com.example.dminfo.model.Administrador;
import com.example.dminfo.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministradorController {

    @Autowired
    private Administrador adminModel;

    public Administrador buscar(int id) {
        return adminModel.getById(id, SingletonDB.getConexao());
    }

    public List<Administrador> filtrar(String nome, String dtIni, String dtFim) {
        return adminModel.filtrar(nome, dtIni, dtFim, SingletonDB.getConexao());
    }

    public List<Administrador> listar() {
        return adminModel.listarTodos(SingletonDB.getConexao());
    }

    public Administrador salvar(Administrador administrador) {
        if (administrador == null || administrador.getUsuario() == null) {
            throw new RuntimeException("Dados do administrador incompletos.");
        }

        adminModel.setUsuario(administrador.getUsuario());
        adminModel.setDtIni(administrador.getDtIni());
        adminModel.setDtFim(administrador.getDtFim());

        return adminModel.salvar(SingletonDB.getConexao());
    }

    public Administrador update(int id, Administrador adminDetails) {
        return adminModel.atualizarDtFim(id, adminDetails, SingletonDB.getConexao());
    }

    public boolean excluir(int id) {
        return adminModel.excluir(id, SingletonDB.getConexao());
    }
}