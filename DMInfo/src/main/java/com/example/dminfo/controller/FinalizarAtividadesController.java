package com.example.dminfo.controller;

import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.example.dminfo.util.SingletonDB;
import com.example.dminfo.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinalizarAtividadesController {

    @Autowired
    private CriarRealizacaoAtividades atividadeModel;

    public List<CriarRealizacaoAtividades> listarTodas() {
        return atividadeModel.listarTodas(SingletonDB.getConexao());
    }

    public boolean finalizarAtividade(CriarRealizacaoAtividades dadosNovos) {
        Conexao conexao = SingletonDB.getConexao();

        CriarRealizacaoAtividades existente = atividadeModel.buscarPorId(dadosNovos.getId(), conexao);

        if (existente == null)
            return false;

        if (dadosNovos.getDtIni() != null)
            existente.setDtIni(dadosNovos.getDtIni());
        if (dadosNovos.getDtFim() != null)
            existente.setDtFim(dadosNovos.getDtFim());

        existente.setCustoreal(dadosNovos.getCustoreal());

        if (dadosNovos.getObservacoes() != null)
            existente.setObservacoes(dadosNovos.getObservacoes());
        if (dadosNovos.getStatus() != null)
            existente.setStatus(dadosNovos.getStatus());

        return atividadeModel.finalizar(existente, conexao);
    }
}