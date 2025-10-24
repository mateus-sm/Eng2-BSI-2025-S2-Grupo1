package com.example.dminfo.services;

import com.example.dminfo.model.Conquista;
import com.example.dminfo.repositories.ConquistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConquistaService {
    @Autowired
    private ConquistaRepository repo;

    public List<Conquista> listar() {
        return repo.findAll();
    }

    public Conquista salvar(Conquista conquista) {
        return repo.save(conquista);
    }

    public void excluir(Integer id) {
        repo.deleteById(id);
    }
}
