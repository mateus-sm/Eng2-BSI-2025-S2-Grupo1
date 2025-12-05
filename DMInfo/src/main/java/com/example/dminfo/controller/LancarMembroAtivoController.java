package com.example.dminfo.controller;

import com.example.dminfo.model.LancarMembroAtivo;
import com.example.dminfo.model.Membro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LancarMembroAtivoController {

    @Autowired
    private LancarMembroAtivo model;

    public List<Membro> listarTodos() {
        return model.listarTodos();
    }

    public void atualizarStatus(Integer id, Membro membro) {
        model.atualizarStatus(id, membro);
    }
}