package com.example.dminfo.repositories;

import com.example.dminfo.model.Membro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembroRepository extends JpaRepository<Membro, Integer> {
}
