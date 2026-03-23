package com.example.dminfo.model.state;

import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.example.dminfo.dao.CriarRealizacaoAtividadesDAO;
import com.example.dminfo.util.Conexao;

public class EstadoAtividadeAtiva implements EstadoAtividade {

    @Override
    public boolean finalizar(CriarRealizacaoAtividades atividadeAtualizada, CriarRealizacaoAtividades atividadeNoBanco, CriarRealizacaoAtividadesDAO dao, Conexao conexao) {

        if (atividadeNoBanco.getStatus() != null && atividadeNoBanco.getStatus() == true) {
            EstadoAtividade outraClasse = new EstadoAtividadeFinalizada();
            return outraClasse.finalizar(atividadeAtualizada, atividadeNoBanco, dao, conexao);
        }

        return dao.finalizarAtividade(atividadeAtualizada, conexao);
    }
}