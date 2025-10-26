package com.example.dminfo.services;

import com.example.dminfo.model.Evento;
import com.example.dminfo.repositories.EventoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    public List<Evento> listar() {
        return eventoRepository.findAll();
    }

    @Transactional
    public Evento salvar(Evento evento) {
        evento.setId(0);
        return eventoRepository.save(evento);
    }

    public Evento getById(Integer id) {
        return eventoRepository.findById(id).orElse(null);
    }

    public boolean excluir(Integer id) {
        if (eventoRepository.existsById(id)) {
            eventoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
