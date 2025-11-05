package com.example.dminfo.controller;

import com.example.dminfo.model.Parametros;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParametrosController { // O novo "Service"

    @Autowired
    private Parametros parametroModel;

    public Parametros exibir() {
        return parametroModel.exibir();
    }

    public Parametros salvar(Parametros parametro) {
        return parametroModel.salvar(parametro);
    }

    public void excluir(Integer id) {
        parametroModel.excluir(id);
    }

    public boolean existeParametro() {
        return parametroModel.existeParametro();
    }
}