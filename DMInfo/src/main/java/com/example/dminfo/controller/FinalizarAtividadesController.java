package com.example.dminfo.controller;

import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.example.dminfo.model.observer.CalendarioLocalObserver;
import com.example.dminfo.model.observer.NotificacaoObserver;
import com.example.dminfo.util.SingletonDB;
import com.example.dminfo.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinalizarAtividadesController {

    @Autowired
    private CriarRealizacaoAtividades atividadeModel;

    @Autowired
    private CalendarioLocalObserver calendarioLocalObserver;

    @Autowired
    private NotificacaoObserver notificacaoObserver;

    public List<CriarRealizacaoAtividades> listarTodas() {
        return atividadeModel.listarTodas(SingletonDB.getConexao());
    }

    public boolean finalizarAtividade(CriarRealizacaoAtividades dadosNovos) {
        Conexao conexao = SingletonDB.getConexao();

        CriarRealizacaoAtividades existente = atividadeModel.buscarPorId(dadosNovos.getId(), conexao);

        if (existente == null) {
            return false;
        }

        // Padrão Observer: Adicionamos os observadores
        existente.add(calendarioLocalObserver);
        existente.add(notificacaoObserver);

        existente.setDtIni(dadosNovos.getDtIni());
        existente.setDtFim(dadosNovos.getDtFim());
        existente.setCustoreal(dadosNovos.getCustoreal());

        if (dadosNovos.getObservacoes() != null) {
            existente.setObservacoes(dadosNovos.getObservacoes());
        }

        if (dadosNovos.getStatus() != null) {
            existente.setStatus(dadosNovos.getStatus());
        }

        return atividadeModel.finalizar(existente, conexao);
    }
}