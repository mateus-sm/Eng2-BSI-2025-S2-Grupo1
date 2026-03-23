package com.example.dminfo.model.state;

import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.example.dminfo.dao.CriarRealizacaoAtividadesDAO;
import com.example.dminfo.util.Conexao;

public interface EstadoAtividade {
    boolean finalizar(CriarRealizacaoAtividades atividadeAtualizada, CriarRealizacaoAtividades atividadeNoBanco, CriarRealizacaoAtividadesDAO dao, Conexao conexao);
}