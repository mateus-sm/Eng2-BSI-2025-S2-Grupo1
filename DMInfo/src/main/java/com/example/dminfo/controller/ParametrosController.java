package com.example.dminfo.controller;

import com.example.dminfo.model.Parametros;
import com.example.dminfo.util.Conexao;
import com.example.dminfo.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParametrosController {

    @Autowired
    private Parametros parametrosModel; // Comunica com o Model

    public Parametros exibir() {
        return parametrosModel.exibir(SingletonDB.getConexao());
    }

    public Parametros salvar(Parametros dados) {
        return parametrosModel.salvar(dados, SingletonDB.getConexao());
    }

    public void excluir(Integer id) {
        parametrosModel.excluir(id, SingletonDB.getConexao());
    }

    public boolean existeParametro() {
        return parametrosModel.existeParametro(SingletonDB.getConexao());
    }
}