package com.example.dminfo.controller;

import com.example.dminfo.model.Doacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoacaoController {

    @Autowired
    private Doacao doacaoModel;

    public List<Doacao> listar() {
        return doacaoModel.listar();
    }

    public Doacao buscar(int id) {
        return doacaoModel.buscar(id);
    }

    public Doacao salvar(Doacao doacao) {
        return doacaoModel.salvar(doacao);
    }

    public Doacao atualizar(Doacao doacao) {
        return doacaoModel.atualizar(doacao);
    }

    public boolean excluir(int id) {
        return doacaoModel.excluir(id);
    }
}