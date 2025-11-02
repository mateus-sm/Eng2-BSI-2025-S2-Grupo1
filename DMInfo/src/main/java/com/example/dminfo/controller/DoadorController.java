package com.example.dminfo.controller;

import com.example.dminfo.model.Doador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // É um Service, não um RestController
public class DoadorController {

    @Autowired
    private Doador doadorModel; // Injeta o "Fat Model"

    public List<Doador> listar() {
        return doadorModel.listar();
    }

    public Doador getById(Integer id) {
        return doadorModel.getById(id);
    }

    public Doador salvar(Doador doador) {
        return doadorModel.salvar(doador);
    }

    public Doador atualizar(Integer id, Doador doador) {
        return doadorModel.atualizar(id, doador);
    }

    public boolean excluir(Integer id) {
        return doadorModel.excluir(id);
    }
}