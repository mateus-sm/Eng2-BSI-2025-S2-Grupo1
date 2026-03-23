package com.example.dminfo.model.state;

import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.example.dminfo.dao.CriarRealizacaoAtividadesDAO;
import com.example.dminfo.util.Conexao;

public class EstadoAtividadeAtiva implements EstadoAtividade {

    @Override
    public boolean finalizar(CriarRealizacaoAtividades atividadeAtualizada, CriarRealizacaoAtividadesDAO dao, Conexao conexao) {
        return dao.finalizarAtividade(atividadeAtualizada, conexao);
    }
}