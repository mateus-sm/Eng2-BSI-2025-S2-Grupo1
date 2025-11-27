package com.example.dminfo.model;

import com.example.dminfo.dao.CalendarioDAO;
import com.example.dminfo.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class Calendario {
    private int id_calendario;
    private CriarRealizacaoAtividades id_criacao;

    @Autowired
    private CalendarioDAO dao;

    public Calendario() {
    }

    public Calendario(CriarRealizacaoAtividades id_criacao) {
        this.id_criacao = id_criacao;
    }

    public int getId_calendario() {return id_calendario;}
    public void setId_calendario(int id_calendario) {this.id_calendario = id_calendario;}

    public CriarRealizacaoAtividades getId_criacao() {return id_criacao;}
    public void setId_criacao(CriarRealizacaoAtividades id_criacao) {
        this.id_criacao = id_criacao;
    }


    public List<CriarRealizacaoAtividades> listarTodasAtividades(Conexao conexao) {
        return dao.listarTodasAtividades(conexao);
    }

    public List<Integer> listarAtividadesAtivasIds(Conexao conexao) {
        return dao.listarAtividadesAtivasIds(conexao);
    }

    public Calendario salvar(Calendario calendario, Conexao conexao) {
        if (calendario == null || calendario.getId_criacao() == null) {
            throw new RuntimeException("Dados inválidos para adicionar ao calendário.");
        }

        return dao.create(calendario, conexao);
    }

    public boolean excluir(Integer idCriacao, Conexao conexao) {
        if (idCriacao == null) {
            throw new RuntimeException("ID inválido para exclusão.");
        }
        return dao.delete(idCriacao, conexao);
    }
}