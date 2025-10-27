package com.example.dminfo.controller;

import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.example.dminfo.services.CriarRealizacaoAtividadesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
