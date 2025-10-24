package com.example.dminfo.controller;

import com.example.dminfo.model.Conquista;
import com.example.dminfo.model.Parametros;
import com.example.dminfo.services.ParametrosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parametrizacao")
public class ParametrosController {
    @Autowired
    private ParametrosService service;

    @GetMapping
    public ResponseEntity<Object> exibir() {
        return ResponseEntity.ok(service.exibir());
    }

    @PostMapping
    public ResponseEntity<Parametros> salvar() {
        return ResponseEntity.status(HttpStatus.CREATED).body(ParametrosService.salvar());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(@PathVariable Integer id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

}
