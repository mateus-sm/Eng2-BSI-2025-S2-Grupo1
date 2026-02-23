package com.example.dminfo.dto;

import java.time.LocalDate;
import java.util.List;

public class DistribuicaoDeRecursosDTO {
    private int idAdmin;
    private LocalDate data;
    private String descricao;
    private String instituicaoReceptora;
    private double valor;

    private List<ItemDistribuicaoDTO> itens;

    // Getters e Setters
    public int getIdAdmin() { return idAdmin; }
    public void setIdAdmin(int idAdmin) { this.idAdmin = idAdmin; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getInstituicaoReceptora() { return instituicaoReceptora; }
    public void setInstituicaoReceptora(String instituicaoReceptora) { this.instituicaoReceptora = instituicaoReceptora; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public List<ItemDistribuicaoDTO> getItens() { return itens; }
    public void setItens(List<ItemDistribuicaoDTO> itens) { this.itens = itens; }
}