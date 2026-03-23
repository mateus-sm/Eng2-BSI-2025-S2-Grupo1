package com.example.dminfo.model.template;

import com.example.dminfo.model.Parametros;
import com.example.dminfo.util.Conexao;

public abstract class SalvarParametrosTemplate {

    public final Parametros executarSalvamento(Parametros parametro, Conexao conexao) {
        validarRegrasDeNegocio(parametro);

        Parametros existente = buscarExistente(conexao);

        if (existente == null)
            return inserirNovo(parametro, conexao);
        else {
            parametro.setId(existente.getId());
            return atualizarExistente(parametro, conexao);
        }
    }

    protected abstract void validarRegrasDeNegocio(Parametros parametro);
    protected abstract Parametros buscarExistente(Conexao conexao);
    protected abstract Parametros inserirNovo(Parametros parametro, Conexao conexao);
    protected abstract Parametros atualizarExistente(Parametros parametro, Conexao conexao);
}