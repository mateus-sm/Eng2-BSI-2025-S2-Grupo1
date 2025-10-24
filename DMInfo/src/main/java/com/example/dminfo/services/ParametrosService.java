package com.example.dminfo.services;


import com.example.dminfo.model.Parametros;
import com.example.dminfo.repositories.ParametrosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParametrosService {
    @Autowired
    private static ParametrosRepository parametrosRepository;

    public List<Parametros> exibir() { return parametrosRepository.findAll(); }

    public static Parametros salvar(){
        Parametros unico = new Parametros(
                "Capítulo Ivair Salomão Liboni",
                "Capítulo Ivair Salomão Liboni",
                "Capítulo Ivair Salomão Liboni número 905 da Ordem DeMolay",
                "Rua Martins Francisco, 523",
                "Centro",
                "Regente Feijó",
                "19573-034",
                "SP",
                "(18)99653-0977",
                "xxxxxxxxxxxxxxxxxxxxx",
                "xxxxxxxxxxxxxxxxxxxxx",
                "99.999.999/9999-99",
                "xxxxxxxxxxxxxxxxxxxxx",
                "xxxxxxxxxxxxxxxxxxxxx"
        );

        return parametrosRepository.save(unico);
    }

    public void excluir(Integer id) {
        parametrosRepository.deleteById(id);
    }
 }
