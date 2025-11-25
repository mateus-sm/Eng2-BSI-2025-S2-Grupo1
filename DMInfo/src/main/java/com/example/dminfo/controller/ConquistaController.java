package com.example.dminfo.controller;

import com.example.dminfo.model.Conquista;
import com.example.dminfo.util.Conexao;
import com.example.dminfo.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConquistaController {

    @Autowired
    private Conquista ConquistaModel;

    public List<Conquista> listar() {
        return ConquistaModel.listar("", SingletonDB.getConexao());
    }

    public Conquista salvar(Conquista conquista) {
        if (conquista == null || conquista.getDescricao() == null || conquista.getDescricao().isEmpty()) {
            throw new RuntimeException("Objeto Conquista inconsistente.");
        }

        Conexao conexao = SingletonDB.getConexao();
        Conquista existente = ConquistaModel.getByDesc(conquista, conexao);
        if (existente != null) {
            throw new RuntimeException("Conquista já existe.");
        }

        return ConquistaModel.salvar(conquista,  conexao);
    }

    public boolean atualizar(Conquista conquista) {
        if (conquista == null || conquista.getDescricao() == null || conquista.getDescricao().isEmpty()) {
            throw new RuntimeException("Objeto Conquista inconsistente.");
        }

        Conquista c = ConquistaModel.update(conquista.getId(), conquista, SingletonDB.getConexao());
        return c != null;
    }

    public Conquista getById(Integer id) {
        Conquista conquista = ConquistaModel.getById(id, SingletonDB.getConexao());
        if (conquista == null) {
            throw new RuntimeException("Conquista não encontrada.");
        }
        return conquista;
    }

    public void excluir(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID inválido para exclusão.");
        }
        ConquistaModel.excluir(id, SingletonDB.getConexao());
    }
}
