package com.example.dminfo.repositories;


import com.example.dminfo.model.Conquista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConquistaRepository extends JpaRepository<Conquista, Integer> {
    Optional<Conquista> findByDescricao(String descricao);
}