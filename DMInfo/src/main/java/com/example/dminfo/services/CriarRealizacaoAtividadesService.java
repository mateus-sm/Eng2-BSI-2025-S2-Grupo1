package com.example.dminfo.services;

import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.example.dminfo.repositories.CriarRealizacaoAtividadesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CriarRealizacaoAtividadesService {
    @Autowired
    private CriarRealizacaoAtividadesRepository repository;

    public List<CriarRealizacaoAtividades> listar() {
        return repository.findAllDetalhes();
    }
}
