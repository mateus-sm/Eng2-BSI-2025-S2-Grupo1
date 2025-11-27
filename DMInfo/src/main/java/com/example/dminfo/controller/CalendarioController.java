package com.example.dminfo.controller;

import com.example.dminfo.model.Calendario;
import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.example.dminfo.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalendarioController {

    @Autowired
    private Calendario calendarioModel;

    public List<CriarRealizacaoAtividades> listarTodasAtividades() {
        return calendarioModel.listarTodasAtividades(SingletonDB.getConexao());
    }

    public List<Integer> listarAtividadesAtivasIds() {
        return calendarioModel.listarAtividadesAtivasIds(SingletonDB.getConexao());
    }

    public boolean adicionarAtividadeAoCalendario(Integer idCriacao) {
        if (idCriacao == null) {
            throw new RuntimeException("ID inválido.");
        }

        CriarRealizacaoAtividades atividade = new CriarRealizacaoAtividades();
        atividade.setId(idCriacao);

        Calendario cal = new Calendario();
        cal.setId_criacao(atividade);

        Calendario resultado = calendarioModel.salvar(cal, SingletonDB.getConexao());

        return resultado != null;
    }

    public boolean removerAtividadeDoCalendario(Integer idCriacao) {
        return calendarioModel.excluir(idCriacao, SingletonDB.getConexao());
    }
}