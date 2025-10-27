package com.example.dminfo.services;


import com.example.dminfo.model.Parametros;
import com.example.dminfo.repositories.ParametrosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParametrosService {
    @Autowired
    private ParametrosRepository parametrosRepository;

    public List<Parametros> exibir() { return parametrosRepository.findAll(); }

    public Parametros salvar(Parametros parametro){
        return parametrosRepository.save(parametro);
    }

    public void excluir(Integer id) {
        parametrosRepository.deleteById(id);
    }

    public boolean existeParametro() {
        return parametrosRepository.count() > 0;
    }
 }
