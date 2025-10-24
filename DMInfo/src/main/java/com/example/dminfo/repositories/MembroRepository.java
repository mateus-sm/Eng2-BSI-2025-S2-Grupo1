package com.example.dminfo.repositories;

import com.example.dminfo.model.Membro;
import com.example.dminfo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembroRepository extends JpaRepository<Membro, Integer> {
    Optional<Membro> findByUsuario(Usuario usuario);
}
