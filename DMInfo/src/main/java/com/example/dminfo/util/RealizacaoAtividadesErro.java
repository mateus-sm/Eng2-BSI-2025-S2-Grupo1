package com.example.dminfo.util;

import java.time.LocalDateTime;

public class RealizacaoAtividadesErro {
    private LocalDateTime timestamp;
    private String mensagem;

    public RealizacaoAtividadesErro(String mensagem) {
        this.timestamp = LocalDateTime.now();
        this.mensagem = mensagem;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}