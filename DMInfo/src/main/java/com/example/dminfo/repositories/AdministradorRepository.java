package com.example.dminfo.repositories;

import com.example.dminfo.model.Administrador;
import com.example.dminfo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {
    Optional<Administrador> findByUsuario(Usuario usuario);
}
