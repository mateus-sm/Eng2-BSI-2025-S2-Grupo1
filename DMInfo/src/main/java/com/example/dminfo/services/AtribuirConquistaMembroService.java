package com.example.dminfo.services;

import com.example.dminfo.model.AtribuirConquistaMembro;
import com.example.dminfo.repositories.AtribuirConquistaMembroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AtribuirConquistaMembroService {
    @Autowired
    private AtribuirConquistaMembroRepository repo;

    public List<AtribuirConquistaMembro> listar() {
        return repo.findAll();
    }

    public AtribuirConquistaMembro salvar(AtribuirConquistaMembro conqMembro) {
        return repo.save(conqMembro);
    }

    public void excluir(Integer id) {
        repo.deleteById(id);
    }
}
