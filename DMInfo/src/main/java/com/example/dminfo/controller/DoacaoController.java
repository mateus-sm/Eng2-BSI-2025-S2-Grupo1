package com.example.dminfo.controller;

import com.example.dminfo.dao.AdministradorDAO;
import com.example.dminfo.dao.DoacaoDAO;
import com.example.dminfo.dao.DoadorDAO;
import com.example.dminfo.model.Administrador;
import com.example.dminfo.model.Doacao;
import com.example.dminfo.model.Doador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DoacaoController {

    @Autowired private DoacaoDAO dao;
    @Autowired private DoadorDAO doadorDAO;
    @Autowired private AdministradorDAO adminDAO;

    private void validarDoacao(Doacao doacao) {
        // Validação 1: ID de Doador e Admin são obrigatórios
        if (doacao.getId_doador() == null || doacao.getId_doador().getId() == 0)
            throw new RuntimeException("O ID do Doador é obrigatório.");
        if (doacao.getId_admin() == null || doacao.getId_admin().getId() == 0)
            throw new RuntimeException("O ID do Administrador é obrigatório.");

        // Validação 2: Doador e Admin existem no banco de dados
        Doador doador = doadorDAO.get(doacao.getId_doador().getId());
        if (doador == null)
            throw new RuntimeException("Doador não encontrado com ID: " + doacao.getId_doador().getId());

        Administrador admin = adminDAO.get(doacao.getId_admin().getId());
        if (admin == null)
            throw new RuntimeException("Administrador não encontrado com ID: " + doacao.getId_admin().getId());

        // Validação 3: Valor deve ser positivo
        if (doacao.getValor() <= 0)
            throw new RuntimeException("O valor da doação deve ser positivo.");

        doacao.setId_doador(doador);
        doacao.setId_admin(admin);
    }


    public List<Doacao> listar() {return dao.get("");}

    public Doacao buscar(int id) {return dao.get(id);}

    public Doacao salvar(Doacao doacao) {
        validarDoacao(doacao);
        doacao.setData(LocalDate.now());

        return dao.gravar(doacao);
    }

    public Doacao atualizar(Doacao doacao) {
        if (dao.get(doacao.getId_doacao()) == null)
            throw new RuntimeException("Doação não encontrada com ID: " + doacao.getId_doacao());

        validarDoacao(doacao);

        if (dao.atualizar(doacao))
            return dao.get(doacao.getId_doacao());

        return null;
    }

    public boolean excluir(int id) {
        if (dao.get(id) == null)
            throw new RuntimeException("Doação não encontrada com ID: " + id);

        return dao.excluir(id);
    }
}