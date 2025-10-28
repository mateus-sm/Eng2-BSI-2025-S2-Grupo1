package com.example.dminfo.repositories;

import com.example.dminfo.model.CriarRealizacaoAtividades;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CriarRealizacaoAtividadesRepository extends JpaRepository<CriarRealizacaoAtividades, Integer> {
    @Query("SELECT cra FROM CriarRealizacaoAtividades cra LEFT JOIN FETCH cra.admin ad LEFT JOIN FETCH ad.usuario LEFT JOIN FETCH cra.atv a LEFT JOIN FETCH a.evento ORDER BY a.descricao ASC")
    List<CriarRealizacaoAtividades> findAllDetalhes();
}
