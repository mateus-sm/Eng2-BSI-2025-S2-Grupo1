package com.example.dminfo.model;

import java.time.LocalDateTime;

public class FrequenciaAtividade {

    private int id;
    // Referência à ocorrência da atividade (tabela criar_realizacao_atividades)
    private CriarRealizacaoAtividades atividadeRealizada;
    private Membro membro;
    private Boolean statusParticipacao; // TRUE para Presente
    private LocalDateTime dataRegistro;

    // Construtor
    public FrequenciaAtividade() {
        this.statusParticipacao = true; // Padrão: TRUE (Presente)
        this.dataRegistro = LocalDateTime.now();
    }

    // --- Getters e Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public CriarRealizacaoAtividades getAtividadeRealizada() { return atividadeRealizada; }
    public void setAtividadeRealizada(CriarRealizacaoAtividades atividadeRealizada) { this.atividadeRealizada = atividadeRealizada; }

    public Membro getMembro() { return membro; }
    public void setMembro(Membro membro) { this.membro = membro; }

    public Boolean getStatusParticipacao() { return statusParticipacao; }
    public void setStatusParticipacao(Boolean statusParticipacao) { this.statusParticipacao = statusParticipacao; }

    public LocalDateTime getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDateTime dataRegistro) { this.dataRegistro = dataRegistro; }
}