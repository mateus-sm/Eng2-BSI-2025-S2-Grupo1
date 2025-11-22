package com.example.dminfo.controller;

import com.example.dminfo.dao.MembroDAO;
import com.example.dminfo.dao.MensalidadeDAO;
import com.example.dminfo.model.Mensalidade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MensalidadeController {

    @Autowired
    private MensalidadeDAO dao;

    @Autowired
    private MembroDAO mdao;

    public Mensalidade salvar(Mensalidade m) {
        if (m != null && mdao.get(m.getId_membro()) != null && m.getValor() > 0) { //achou membro no banco
            dao.gravar(m);
            return m;
        } else {
            throw new RuntimeException("Mensalidade Inválida");
        }
    }

    public boolean excluir(Integer id) {
        return dao.excluir(id);
    }

    public Mensalidade getById(Integer id) {
        return dao.buscarPorId(id);
    }

    public List<Mensalidade> listar(String filtroNome) {
        return dao.listar(filtroNome);
    }

    public List<Mensalidade> listarMesAno(int mes, int ano) {
        return dao.listarMesAno(mes, ano);
    }

    public List<Mensalidade> listarMembro(Integer id) {
        return dao.listarMembro(id);
    }
}