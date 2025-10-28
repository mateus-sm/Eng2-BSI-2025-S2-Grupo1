package com.example.dminfo.services;

import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.example.dminfo.repositories.CriarRealizacaoAtividadesRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CriarRealizacaoAtividadesService {
    @Autowired
    private CriarRealizacaoAtividadesRepository repository;

    public List<CriarRealizacaoAtividades> listar() {
        return repository.findAllDetalhes();
    }

    @Transactional
    public void atualizarDataFim(Integer idAtividade, String dataStr) {
        LocalDate dataFim;
        if (dataStr == null || dataStr.isEmpty())
            dataFim = null;
        else
            dataFim = LocalDate.parse(dataStr);

        CriarRealizacaoAtividades atividade = repository.findById(idAtividade).orElseThrow(() -> new RuntimeException("Atividade não encontrada com ID: " + idAtividade));

        atividade.setDtFim(dataFim);
        repository.save(atividade);
    }
}