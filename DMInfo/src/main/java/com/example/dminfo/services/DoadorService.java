package com.example.dminfo.services;

import com.example.dminfo.model.Doador;
import com.example.dminfo.repositories.DoadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoadorService {
    @Autowired
    private DoadorRepository doadorRepository;

    public List<Doador> exibir() { return doadorRepository.findAll(); }

    public Doador salvar(Doador doador){
        return doadorRepository.save(doador);
    }

    public void excluir(Integer id) {
        doadorRepository.deleteById(id);
    }
}