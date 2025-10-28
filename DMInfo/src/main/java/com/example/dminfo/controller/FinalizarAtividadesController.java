package com.example.dminfo.controller;

import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.example.dminfo.services.CriarRealizacaoAtividadesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finalizar-atividades")
public class FinalizarAtividadesController {
    @Autowired
    private CriarRealizacaoAtividadesService service;

    @GetMapping
    public ResponseEntity<List<CriarRealizacaoAtividades>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PutMapping("/{id}/data-fim")
    public ResponseEntity<Object> atualizarData(@PathVariable int id, @RequestBody(required = false) String dataFinalStr) {
        try {
            service.atualizarDataFim(id, dataFinalStr);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
