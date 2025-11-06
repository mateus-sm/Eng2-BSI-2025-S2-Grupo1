package com.example.dminfo.controller;

import com.example.dminfo.dao.CriarRealizacaoAtividadesDAO;
import com.example.dminfo.model.CriarRealizacaoAtividades;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class FinalizarAtividadesController {

    @Autowired
    private CriarRealizacaoAtividadesDAO atividadeDAO;

    public List<CriarRealizacaoAtividades> listarTodas() {
        return atividadeDAO.listarTodas();
    }

    public boolean finalizarAtividade(CriarRealizacaoAtividades atividade) {

        CriarRealizacaoAtividades existente = atividadeDAO.getById(atividade.getId());

        if (existente == null) {
            return false;
        }

        existente.setDtIni(atividade.getDtIni());

        existente.setDtFim(atividade.getDtFim());
        existente.setCustoreal(atividade.getCustoreal());
        existente.setObservacoes(atividade.getObservacoes());


        if (atividade.getStatus() != null) {
            existente.setStatus(atividade.getStatus());
        }

        return atividadeDAO.finalizarAtividade(existente);
    }
}