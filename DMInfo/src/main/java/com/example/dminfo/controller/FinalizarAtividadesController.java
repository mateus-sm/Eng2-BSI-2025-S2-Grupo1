package com.example.dminfo.controller;

import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.example.dminfo.model.observer.AtividadeObserver;
import com.example.dminfo.util.SingletonDB;
import com.example.dminfo.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinalizarAtividadesController {

    @Autowired
    private CriarRealizacaoAtividades atividadeModel;

    @Autowired(required = false)
    private List<AtividadeObserver> observers;

    public List<CriarRealizacaoAtividades> listarTodas() {
        return atividadeModel.listarTodas(SingletonDB.getConexao());
    }

    public boolean finalizarAtividade(CriarRealizacaoAtividades dadosNovos) {
        Conexao conexao = SingletonDB.getConexao();

        CriarRealizacaoAtividades existente = atividadeModel.buscarPorId(dadosNovos.getId(), conexao);

        if (existente == null)
            return false;

        existente.setDtIni(dadosNovos.getDtIni());
        existente.setDtFim(dadosNovos.getDtFim());

        existente.setCustoreal(dadosNovos.getCustoreal());

        if (dadosNovos.getObservacoes() != null)
            existente.setObservacoes(dadosNovos.getObservacoes());

        if (dadosNovos.getStatus() != null)
            existente.setStatus(dadosNovos.getStatus());

        // 3. CAPTURAR O RESULTADO DO STATE
        boolean sucesso = atividadeModel.finalizar(existente, conexao);

        // 4. NOTIFICAR OS OBSERVADORES (CALENDÁRIO)
        if (sucesso && observers != null) {
            for (AtividadeObserver observer : observers) {
                observer.onAtividadeAtualizada(existente);
            }
        }

        return sucesso;
    }
}