package com.example.dminfo.model;

import com.example.dminfo.dao.AdministradorDAO;
import com.example.dminfo.dao.DoacaoDAO;
import com.example.dminfo.dao.DoadorDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository // Não é mais @Entity
public class Doacao {

    // --- Campos POJO ---
    private int id_doacao;
    private Doador id_doador; // Referência ao "Fat Model"
    private Administrador id_admin; // Referência ao "Fat Model"
    private LocalDate data;
    private double valor;
    private String observacao;

    // --- Injeção dos DAOs ---
    @Autowired
    private DoacaoDAO dao;
    @Autowired
    private DoadorDAO doadorDAO;
    @Autowired
    private AdministradorDAO adminDAO;

    // --- Construtores ---
    public Doacao() {}

    // Construtor antigo adaptado
    public Doacao(Doador id_doador, Administrador id_admin, LocalDate data, double valor, String observacao) {
        this.id_doador = id_doador;
        this.id_admin = id_admin;
        this.data = data;
        this.valor = valor;
        this.observacao = observacao;
    }

    // --- Getters e Setters (iguais aos da entidade) ---
    public int getId_doacao() {return id_doacao;}
    public void setId_doacao(int id_doacao) {this.id_doacao = id_doacao;}
    public Doador getId_doador() {return id_doador;}
    public void setId_doador(Doador id_doador) {this.id_doador = id_doador;}
    public Administrador getId_admin() {return id_admin;}
    public void setId_admin(Administrador id_admin) {this.id_admin = id_admin;}
    public LocalDate getData() {return data;}
    public void setData(LocalDate data) {this.data = data;}
    public double getValor() {return valor;}
    public void setValor(double valor) {this.valor = valor;}
    public String getObservacao() {return observacao;}
    public void setObservacao(String observacao) {this.observacao = observacao;}

    // --- LÓGICA DE NEGÓCIOS ---

    public List<Doacao> listar() {
        return dao.get(""); // Filtro vazio
    }

    public Doacao salvar(Doacao doacao) {
        // Validação 1: Doador existe?
        Doador doador = doadorDAO.get(doacao.getId_doador().getId());
        if (doador == null) {
            throw new RuntimeException("Doador não encontrado com ID: " + doacao.getId_doador().getId());
        }

        // Validação 2: Admin existe?
        Administrador admin = adminDAO.get(doacao.getId_admin().getId());
        if (admin == null) {
            throw new RuntimeException("Administrador não encontrado com ID: " + doacao.getId_admin().getId());
        }

        // Validação 3: Valor deve ser positivo
        if (doacao.getValor() <= 0) {
            throw new RuntimeException("O valor da doação deve ser positivo.");
        }

        // Regras de negócio
        doacao.setData(LocalDate.now());
        doacao.setId_doador(doador); // Seta o objeto completo
        doacao.setId_admin(admin); // Seta o objeto completo

        return dao.gravar(doacao);
    }
}