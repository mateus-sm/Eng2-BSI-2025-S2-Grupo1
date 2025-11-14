package com.example.dminfo.controller;

import com.example.dminfo.dao.LancarPagamentoMensalidadeDAO;
import com.example.dminfo.model.Conquista;
import com.example.dminfo.model.LancarPagamentoMensalidade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LancarPagamentoMensalidadeController {

    @Autowired
    private LancarPagamentoMensalidadeDAO LPMdao;

    public List<LancarPagamentoMensalidade> listarMesAno(LancarPagamentoMensalidade LPM) {
        return LPMdao.listarMesAno(LPM.getMes(), LPM.getAno());
    }

    public List<LancarPagamentoMensalidade> listarMembro(LancarPagamentoMensalidade LPM) {
        return LPMdao.listarMembro(LPM.getId_membro());
    }

    public LancarPagamentoMensalidade salvar(LancarPagamentoMensalidade LPM) {
        if (LPM == null || LPM.getData_pagamento().isAfter(LocalDate.now())) {
            throw new RuntimeException("Objeto Lançar Pagamento Mensalidade inconsistente.");
        }

        return LPMdao.gravar(LPM);
    }

    public boolean atualizar(LancarPagamentoMensalidade LPM) {
        if (LPM == null || LPM.getData_pagamento().isAfter(LocalDate.now())) {
            throw new RuntimeException("Objeto Lançar Pagamento Mensalidade inconsistente.");
        }

        return LPMdao.alterar(LPM);
    }

    public void excluir(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID inválido para exclusão.");
        }
        LPMdao.excluir(id);
    }
}
