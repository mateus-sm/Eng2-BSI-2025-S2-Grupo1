package com.example.dminfo.controller;

import com.example.dminfo.model.Doador;
import com.example.dminfo.dao.DoadorDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoadorController {

    @Autowired
    private DoadorDAO dao;

    public List<Doador> listar() {
        return dao.get("");
    }

    public Doador getById(Integer id) {
        return dao.get(id);
    }

    public Doador salvar(Doador doador) {
        if (dao.getByDocumento(doador.getDocumento()) != null)
            throw new RuntimeException("Já existe um doador com este documento.");

        return dao.gravar(doador);
    }

    public Doador atualizar(Integer id, Doador doadorDetalhes) {
        Doador doador = dao.get(id);
        if (doador == null)
            throw new RuntimeException("Doador não encontrado com id: " + id);

        if (!doador.getDocumento().equals(doadorDetalhes.getDocumento()) && dao.getByDocumento(doadorDetalhes.getDocumento()) != null)
            throw new RuntimeException("O novo documento já pertence a outro doador.");

        doador.setNome(doadorDetalhes.getNome());
        doador.setDocumento(doadorDetalhes.getDocumento());
        doador.setRua(doadorDetalhes.getRua());
        doador.setBairro(doadorDetalhes.getBairro());
        doador.setCidade(doadorDetalhes.getCidade());
        doador.setUf(doadorDetalhes.getUf());
        doador.setCep(doadorDetalhes.getCep());
        doador.setEmail(doadorDetalhes.getEmail());
        doador.setTelefone(doadorDetalhes.getTelefone());
        doador.setContato(doadorDetalhes.getContato());

        if (dao.alterar(doador))
            return doador;

        throw new RuntimeException("Erro ao atualizar doador no banco de dados.");
    }

    public boolean excluir(Integer id){
        if (dao.get(id) == null)
            throw new RuntimeException("Doador não encontrado com id: " + id);

        return dao.excluir(id);
    }
}