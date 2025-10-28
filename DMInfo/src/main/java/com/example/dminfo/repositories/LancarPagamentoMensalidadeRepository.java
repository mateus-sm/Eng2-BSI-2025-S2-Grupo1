package com.example.dminfo.repositories;

import com.example.dminfo.model.LancarPagamentoMensalidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LancarPagamentoMensalidadeRepository extends JpaRepository<LancarPagamentoMensalidade, Integer> {
    List<LancarPagamentoMensalidade> findByMesAndAno(int mes, int ano);
    List<LancarPagamentoMensalidade> findByAno(int ano);
}
