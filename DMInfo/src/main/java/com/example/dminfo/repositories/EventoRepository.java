package com.example.dminfo.repositories;

import com.example.dminfo.model.Evento;
import com.example.dminfo.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
    Optional<Evento> findByAdm(Administrador adm);
}
