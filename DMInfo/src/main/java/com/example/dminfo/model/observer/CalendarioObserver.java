package com.example.dminfo.model.observer;

import com.example.dminfo.controller.CalendarioController;
import com.example.dminfo.model.CriarRealizacaoAtividades;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalendarioObserver implements AtividadeObserver {

    @Autowired
    private CalendarioController calendarioController;

    @Override
    public void onAtividadeAtualizada(CriarRealizacaoAtividades atividade) {
        //Se a atividade for finalizada ou atualizada, tenta adicionar ao calendário
        try {
            calendarioController.adicionarAtividadeAoCalendario(atividade.getId());
            System.out.println("Observer: Atividade " + atividade.getId() + " sincronizada com o calendário.");
        } catch (Exception e) {
            // Apanha a exceção silenciosamente caso a atividade já exista no calendário
            System.out.println("Observer (Aviso): A atividade " + atividade.getId() + " já se encontra no calendário ou ocorreu um erro menor.");
        }
    }
}