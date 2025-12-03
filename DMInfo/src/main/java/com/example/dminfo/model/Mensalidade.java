package com.example.dminfo.model;

import com.example.dminfo.dao.MembroDAO;
import com.example.dminfo.dao.MensalidadeDAO;
import com.example.dminfo.util.Conexao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public class Mensalidade {

    private int id_mensalidade;
    private int id_membro;
    private int mes;
    private int ano;
    private Double valor;
    private LocalDate dataPagamento;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String nome_membro;

    @JsonIgnore
    private MensalidadeDAO dao = new MensalidadeDAO();

    @JsonIgnore
    private MembroDAO membroDAO = new MembroDAO();

    public Mensalidade() {}

    public Mensalidade(int id_mensalidade, int id_membro, int mes, int ano, Double valor, LocalDate dataPagamento) {
        this.id_mensalidade = id_mensalidade;
        this.id_membro = id_membro;
        this.mes = mes;
        this.ano = ano;
        this.valor = valor;
        this.dataPagamento = dataPagamento;
    }

    public Mensalidade salvar(Conexao conexao) {
        if (this.valor == null || this.valor <= 0) {
            throw new RuntimeException("Valor precisa ser maior que zero");
        }

        if (this.ano <= 0 || this.mes <= 0 || this.dataPagamento == null) {
            throw new RuntimeException("Data da mensalidade inválida");
        }

        if (membroDAO.get(this.id_membro) == null) {
            throw new RuntimeException("Membro não encontrado no banco");
        }

        Mensalidade duplicada = dao.buscarPorMembroMesAno(this.id_membro, this.mes, this.ano,conexao);

        if (duplicada != null) {

            if (this.id_mensalidade == 0 || this.id_mensalidade != duplicada.getId_mensalidade()) {
                throw new RuntimeException("Este membro já possui uma mensalidade registrada para o Mês " + this.mes + "/" + this.ano);
            }
        }

        Mensalidade existente = null;
        if (this.id_mensalidade > 0) {
            existente = dao.buscarPorId(this.id_mensalidade, conexao);
        }

        if (existente != null) {
            boolean ok = dao.alterar(this, conexao);
            if (!ok) throw new RuntimeException("Erro ao atualizar mensalidade");
            return this;
        } else {
            return dao.gravar(this, conexao);
        }
    }

    public boolean excluir(Conexao conexao) {
        return dao.excluir(this.id_mensalidade,conexao);
    }

    public static Mensalidade buscarPorId(Integer id, Conexao conexao) {
        return new MensalidadeDAO().buscarPorId(id, conexao);
    }

    public static List<Mensalidade> listarTodos(String filtroNome, Conexao conexao) {
        return new MensalidadeDAO().listar(filtroNome, conexao);
    }

    public static List<Mensalidade> listarPorMesAno(int mes, int ano, Conexao conexao) {
        return new MensalidadeDAO().listarMesAno(mes, ano, conexao);
    }

    public static List<Mensalidade> listarPorMembro(Integer idMembro, Conexao conexao) {
        return new MensalidadeDAO().listarMembro(idMembro, conexao);
    }

    public static List<Mensalidade> filtrarAvancado(String nome, String dataIni, String dataFim, Conexao conexao) {
        return new MensalidadeDAO().filtrar(nome, dataIni, dataFim, conexao);
    }


    public int getId_mensalidade() { return id_mensalidade; }
    public void setId_mensalidade(int id_mensalidade) { this.id_mensalidade = id_mensalidade; }
    public int getId_membro() { return id_membro; }
    public void setId_membro(int id_membro) { this.id_membro = id_membro; }
    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }
    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }
    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }
    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }
    public String getNome_membro() {return nome_membro;}
    public void setNome_membro(String nome_membro) {this.nome_membro = nome_membro;}
}