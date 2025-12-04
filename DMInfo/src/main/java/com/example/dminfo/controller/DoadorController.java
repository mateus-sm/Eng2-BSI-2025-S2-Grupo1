package com.example.dminfo.controller;

import com.example.dminfo.model.Doador;
import com.example.dminfo.util.Conexao;
import com.example.dminfo.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoadorController {

    @Autowired
    private Doador doadorModel;

    public List<Doador> listar() {
        return doadorModel.listar("", SingletonDB.getConexao());
    }

    public Doador getById(Integer id) {
        Doador d = doadorModel.getById(id, SingletonDB.getConexao());
        if (d == null) {
            throw new RuntimeException("Doador não encontrado.");
        }
        return d;
    }

    public Doador salvar(Doador doador) {
        if (doador == null || doador.getNome() == null || doador.getNome().isEmpty()) {
            throw new RuntimeException("Objeto Doador inconsistente.");
        }

        Conexao conexao = SingletonDB.getConexao();

        Doador existente = doadorModel.getByDocumento(doador, conexao);
        if (existente != null) {
            throw new RuntimeException("Já existe um doador com este documento.");
        }

        return doadorModel.salvar(doador, conexao);
    }

    public boolean atualizar(Doador doador) {
        if (doador == null || doador.getId() == 0) {
            throw new RuntimeException("Objeto Doador inconsistente ou ID inválido.");
        }

        Doador d = doadorModel.update(doador.getId(), doador, SingletonDB.getConexao());
        return d != null;
    }

    public void excluir(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID inválido para exclusão.");
        }
        doadorModel.excluir(id, SingletonDB.getConexao());
    }
}