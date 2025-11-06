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

    // 3. Mova a lógica de "salvar" do Model (Parametros.java) para cá
    public Parametros salvar(Parametros parametro) {
        // Validação
        if (parametro.getCnpj() == null || parametro.getCnpj().isEmpty()) {
            throw new RuntimeException("CNPJ é obrigatório.");
        }

        Parametros existente = parametroDAO.get(); //
        if (existente == null) {
            // Se não existe, cria (INSERT)
            return parametroDAO.gravar(parametro); //
        } else {
            // Se já existe, atualiza (UPDATE)
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