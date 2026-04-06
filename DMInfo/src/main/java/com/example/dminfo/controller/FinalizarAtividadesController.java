package com.example.dminfo.controller;

import com.example.dminfo.model.Calendario;
import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.example.dminfo.util.SingletonDB;
import com.example.dminfo.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinalizarAtividadesController {

    @Autowired
    private CriarRealizacaoAtividades atividadeModel;

    @Autowired
    private Calendario calendarioModel;

    @Autowired
    private JavaMailSender mailSender;

    public List<CriarRealizacaoAtividades> listarTodas() {
        return atividadeModel.listarTodas(SingletonDB.getConexao());
    }

    public boolean finalizarAtividade(CriarRealizacaoAtividades dadosNovos) {
        Conexao conexao = SingletonDB.getConexao();

        CriarRealizacaoAtividades existente = atividadeModel.buscarPorId(dadosNovos.getId(), conexao);

        if (existente == null) {
            return false;
        }

        existente.setDtIni(dadosNovos.getDtIni());
        existente.setDtFim(dadosNovos.getDtFim());
        existente.setCustoreal(dadosNovos.getCustoreal());

        if (dadosNovos.getObservacoes() != null) {
            existente.setObservacoes(dadosNovos.getObservacoes());
        }

        if (dadosNovos.getStatus() != null) {
            existente.setStatus(dadosNovos.getStatus());
        }

        boolean resultado = atividadeModel.finalizar(existente, conexao);

        if (resultado) {
            calendarioModel.setId_criacao(existente);

            calendarioModel.carregarEInstanciarObservadores(conexao);

            String motivo = "Atenção: O status/detalhes da sua atividade foram atualizados (Finalização).";
            calendarioModel.notificarObservadores(motivo, this.mailSender);
        }

        return resultado;
    }
}