package com.example.dminfo.controller;

import com.example.dminfo.model.Doador;
import com.example.dminfo.util.Conexao;
import com.example.dminfo.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoadorController {

    @Autowired
    private Doador doadorModel;

    public List<Doador> listar() {
        return doadorModel.listar("", SingletonDB.getConexao());
    }

    public Doador getById(Integer id) {
        Doador d = doadorModel.getById(id, SingletonDB.getConexao());
        if (d == null) {
            throw new RuntimeException("Doador não encontrado.");
        }
        return d;
    }

    public Doador salvar(Doador doador) {
        Conexao conexao = SingletonDB.getConexao();

        // Validação de Duplicidade
        if (doadorModel.getByDocumento(doador.getDocumento(), conexao) != null)
            throw new RuntimeException("Já existe um doador com este documento.");

        return doadorModel.salvar(doador, conexao);
    }

    public Doador atualizar(Integer id, Doador doadorDetalhes) {
        Conexao conexao = SingletonDB.getConexao();

        Doador doadorExistente = doadorModel.getById(id, conexao);
        if (doadorExistente == null)
            throw new RuntimeException("Doador não encontrado com id: " + id);

        // Validação de Duplicidade na edição
        Doador outroComMesmoDoc = doadorModel.getByDocumento(doadorDetalhes.getDocumento(), conexao);
        if (outroComMesmoDoc != null && outroComMesmoDoc.getId() != id) {
            throw new RuntimeException("O novo documento já pertence a outro doador.");
        }

        // Atualiza os dados do objeto existente com os novos detalhes
        doadorExistente.setNome(doadorDetalhes.getNome());
        doadorExistente.setDocumento(doadorDetalhes.getDocumento());
        doadorExistente.setRua(doadorDetalhes.getRua());
        doadorExistente.setBairro(doadorDetalhes.getBairro());
        doadorExistente.setCidade(doadorDetalhes.getCidade());
        doadorExistente.setUf(doadorDetalhes.getUf());
        doadorExistente.setCep(doadorDetalhes.getCep());
        doadorExistente.setEmail(doadorDetalhes.getEmail());
        doadorExistente.setTelefone(doadorDetalhes.getTelefone());
        doadorExistente.setContato(doadorDetalhes.getContato());

        return doadorModel.alterar(doadorExistente, conexao);
    }

    public boolean excluir(Integer id){
        if (id == null || id == 0) {
            throw new RuntimeException("ID inválido.");
        }
        return doadorModel.excluir(id, SingletonDB.getConexao());
    }
}