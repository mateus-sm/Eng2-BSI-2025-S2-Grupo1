package com.example.dminfo.services;

import com.example.dminfo.model.Conquista;
import com.example.dminfo.repositories.ConquistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConquistaService {
    @Autowired
    private ConquistaRepository repo;

    public List<Conquista> listar() {
        return repo.findAll();
    }

    public Conquista salvar(Conquista conquista) {
        if (conquista != null) {
            return repo.save(conquista);
        }

        return null;
    }

    public Conquista getById(Integer id) {
        Conquista conquista;
        conquista = repo.findById(id).orElse(null);
        return conquista;
    }

    public boolean excluir(Integer id) {
        Conquista conquista = repo.findById(id).orElse(null);
        if (conquista != null) {
            repo.deleteById(id);
            return true;
        }

        return false;
    }

    public Optional<Conquista> consultar(String descricao) {
        return repo.findByDescricao(descricao);
    }
}
