package com.example.dminfo.controller;

import com.example.dminfo.model.Administrador;
import com.example.dminfo.model.Doacao;
import com.example.dminfo.model.Doador;
import com.example.dminfo.util.Conexao;
import com.example.dminfo.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoacaoController {

    @Autowired
    private Doacao doacaoModel;

    @Autowired
    private Doador doadorModel;

    @Autowired
    private Administrador administradorModel;

    public List<Doacao> listar() {
        return doacaoModel.listar("", SingletonDB.getConexao());
    }

    public Doacao buscar(int id) {
        Doacao d = doacaoModel.getById(id, SingletonDB.getConexao());
        if (d == null) {
            throw new RuntimeException("Doação não encontrada.");
        }
        return d;
    }

    public Doacao salvar(Doacao doacao) {
        Conexao conexao = SingletonDB.getConexao();

        if (doacao == null || doacao.getId_doador() == null || doacao.getId_admin() == null) {
            throw new RuntimeException("Dados da doação incompletos.");
        }

        Doador doador = doadorModel.getById(doacao.getId_doador().getId(), conexao);
        if (doador == null) {
            throw new RuntimeException("Doador informado não encontrado.");
        }

        Administrador admin = administradorModel.getById(doacao.getId_admin().getId(), conexao);
        if (admin == null) {
            throw new RuntimeException("Administrador informado não encontrado.");
        }

        doacao.setId_doador(doador);
        doacao.setId_admin(admin);

        return doacaoModel.salvar(doacao, conexao);
    }

    public Doacao atualizar(Doacao doacao) {
        Conexao conexao = SingletonDB.getConexao();

        if (doacao == null || doacao.getId_doacao() == 0) {
            throw new RuntimeException("Doação inválida para atualização.");
        }

        if (doacaoModel.getById(doacao.getId_doacao(), conexao) == null) {
            throw new RuntimeException("Doação não encontrada.");
        }

        if (doacao.getId_doador() != null && doacao.getId_doador().getId() != 0) {
            Doador d = doadorModel.getById(doacao.getId_doador().getId(), conexao);
            if (d == null) throw new RuntimeException("Novo Doador não encontrado.");
            doacao.setId_doador(d);
        }

        if (doacao.getId_admin() != null && doacao.getId_admin().getId() != 0) {
            Administrador a = administradorModel.getById(doacao.getId_admin().getId(), conexao);
            if (a == null) throw new RuntimeException("Novo Administrador não encontrado.");
            doacao.setId_admin(a);
        }

        doacaoModel.update(doacao.getId_doacao(), doacao, conexao);

        return doacao;
    }

    public boolean excluir(int id) {
        if (id == 0) {
            throw new RuntimeException("ID inválido para exclusão.");
        }
        return doacaoModel.excluir(id, SingletonDB.getConexao());
    }
}