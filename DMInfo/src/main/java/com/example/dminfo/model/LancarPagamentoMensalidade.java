package com.example.dminfo.model;

import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class LancarPagamentoMensalidade {

    private int id_mensalidade;
    private int id_membro;
    private int mes;
    private int ano;
    private double valor;
    private LocalDate data_pagamento;

    public LancarPagamentoMensalidade(){}

    public LancarPagamentoMensalidade(int id_mensalidade, int id_membro, int mes, int ano, double valor, LocalDate data_pagamento) {
        this.id_mensalidade = id_mensalidade;
        this.id_membro = id_membro;
        this.mes = mes;
        this.ano = ano;
        this.valor = valor;
        this.data_pagamento = data_pagamento;
    }

    public int getId_mensalidade() {return id_mensalidade;}
    public void setId_mensalidade(int id_mensalidade) {this.id_mensalidade = id_mensalidade;}

    public int getId_membro() {return id_membro;}
    public void setId_membro(int id_membro) {this.id_membro = id_membro;}

    public int getMes() {return mes;}
    public void setMes(int mes) {this.mes = mes;}

    public int getAno() {return ano;}
    public void setAno(int ano) {this.ano = ano;}

    public double getValor() {return valor;}
    public void setValor(double valor) {this.valor = valor;}

    public LocalDate getData_pagamento() {return data_pagamento;}
    public void setData_pagamento(LocalDate data_pagamento) {this.data_pagamento = data_pagamento;}
}
