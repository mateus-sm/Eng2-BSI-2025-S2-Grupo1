package com.example.dminfo.controller;

import com.example.dminfo.model.Conquista;
import com.example.dminfo.services.ConquistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conquista")
public class ConquistaController {
    @Autowired
    private ConquistaService conquistaService;

    @GetMapping
    public ResponseEntity<Object> listar() {
        return ResponseEntity.ok(conquistaService.listar());
    }

    @PostMapping
    public ResponseEntity<Object> salvar(@RequestBody Conquista conquista) {
        return ResponseEntity.status(HttpStatus.CREATED).body(conquistaService.salvar(conquista));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(@PathVariable Integer id) {
        conquistaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

