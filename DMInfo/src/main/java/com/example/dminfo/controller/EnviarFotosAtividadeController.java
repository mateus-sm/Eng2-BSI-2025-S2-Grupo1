package com.example.dminfo.controller;

import com.example.dminfo.model.EnviarFotosAtividade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnviarFotosAtividadeController {

    @Autowired
    private EnviarFotosAtividade fotoModel; // Injeta o "Fat Model"

    public List<EnviarFotosAtividade> listarPorAtividade(int idAtividade) {
        return fotoModel.listarPorAtividade(idAtividade);
    }

    public EnviarFotosAtividade salvar(EnviarFotosAtividade foto) {
        return fotoModel.salvar(foto);
    }
}