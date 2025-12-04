package com.example.dminfo.controller;

import com.example.dminfo.model.Administrador;
import com.example.dminfo.util.Conexao;
import com.example.dminfo.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministradorController {

    @Autowired
    private Administrador adminModel;

    public Administrador buscar(int id) {
        Administrador admin = adminModel.getById(id, SingletonDB.getConexao());
        if (admin == null) {
            throw new RuntimeException("Administrador não encontrado.");
        }
        return admin;
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
        if (adminDetails == null) {
            throw new RuntimeException("Dados inválidos para atualização.");
        }

        return adminModel.atualizarDtFim(id, adminDetails.getDtFim(), SingletonDB.getConexao());
    }

    public boolean excluir(int id) {
        if (id <= 0) {
            throw new RuntimeException("ID inválido.");
        }
        return adminModel.excluir(id, SingletonDB.getConexao());
    }
}