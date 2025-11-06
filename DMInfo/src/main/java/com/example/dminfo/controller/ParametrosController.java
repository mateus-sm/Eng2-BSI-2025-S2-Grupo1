package com.example.dminfo.controller;

import com.example.dminfo.model.Parametros;
import com.example.dminfo.dao.ParametrosDAO; // 1. Importe o DAO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParametrosController { // O novo "Service"

    // 2. Injete o DAO, não o Model
    @Autowired
    private ParametrosDAO parametroDAO;

    public Parametros exibir() {
        return parametroDAO.get(); //
    }

    public Parametros salvar(Parametros parametro) {
        if (parametro.getRazaoSocial() == null || parametro.getRazaoSocial().trim().isEmpty()) {
            throw new RuntimeException("Razão Social é obrigatória.");
        }
        if (parametro.getRua() == null || parametro.getRua().trim().isEmpty()) {
            throw new RuntimeException("Rua é obrigatória.");
        }
        if (parametro.getCidade() == null || parametro.getCidade().trim().isEmpty()) {
            throw new RuntimeException("Cidade é obrigatória.");
        }
        if (parametro.getTelefone() == null || parametro.getTelefone().trim().isEmpty()) {
            throw new RuntimeException("Telefone é obrigatório.");
        }
        if (parametro.getLogoPequeno() == null || parametro.getLogoPequeno().trim().isEmpty()) {
            throw new RuntimeException("Logo Pequeno é obrigatório.");
        }

        Parametros existente = parametroDAO.get(); //
        if (existente == null) {
            return parametroDAO.gravar(parametro); //
        } else {
            parametro.setId(existente.getId());
            if (parametroDAO.alterar(parametro)) { //
                return parametro;
            }
        }
        throw new RuntimeException("Erro ao salvar parâmetros.");
    }

    public void excluir(Integer id) {
        parametroDAO.excluir(id); //
    }

    public boolean existeParametro() {
        return parametroDAO.count() > 0; //
    }
}