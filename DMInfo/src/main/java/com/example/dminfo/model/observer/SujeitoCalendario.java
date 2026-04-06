package com.example.dminfo.model.observer;

import org.springframework.mail.javamail.JavaMailSender;

public interface SujeitoCalendario {
    public void adicionarObservador(ObserverMembro observador);
    public void removerObservador(ObserverMembro observador);
    public void notificarObservadores(String motivo, JavaMailSender mailSender);
}
