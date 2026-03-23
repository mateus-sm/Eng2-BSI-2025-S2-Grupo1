package com.example.dminfo.model.observer;

import com.example.dminfo.controller.CalendarioController;
import com.example.dminfo.model.CriarRealizacaoAtividades;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalendarioLocalObserver implements Observer {

    @Autowired
    private CalendarioController calendarioController;

    @Override
    public void update(CriarRealizacaoAtividades atividade) {
        try {
            calendarioController.adicionarAtividadeAoCalendario(atividade.getId());
            System.out.println("Observer 1: Calendário Local atualizado.");
        } catch (Exception e) {
        }
    }
}