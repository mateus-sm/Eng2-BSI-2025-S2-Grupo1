package com.example.dminfo.model.observer;

import com.example.dminfo.controller.NotificacaoController;
import com.example.dminfo.model.CriarRealizacaoAtividades;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoObserver implements Observer {

    @Autowired
    private NotificacaoController notificacaoController;

    @Override
    public void update(CriarRealizacaoAtividades atividade) {
        try {
            notificacaoController.enviarNotificacaoManual(atividade.getId());
            System.out.println("Observer 2: Notificação por e-mail disparada com sucesso para a atividade " + atividade.getId());
        } catch (Exception e) {
            System.out.println("Observer 2 Falhou: Não foi possível enviar a notificação. " + e.getMessage());
        }
    }
}