package com.example.dminfo.controller;

import com.example.dminfo.model.Membro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembroController {

    @Autowired
    private Membro membroModel;

    public List<Membro> listar(String filtro) {
        return membroModel.listarComFiltro(filtro);
    }

    public Membro getById(Integer id) {
        return membroModel.getById(id);
    }

    public Membro salvar(Membro membro) {
        return membroModel.salvar(membro);
    }

    public Membro update(Integer id, Membro membroDetails) {
        return membroModel.update(id, membroDetails);
    }

    public boolean excluir(Integer id) {
        return membroModel.excluir(id);
    }
}