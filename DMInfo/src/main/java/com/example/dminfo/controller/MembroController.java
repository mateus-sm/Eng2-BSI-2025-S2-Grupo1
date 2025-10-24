package com.example.dminfo.controller;

import com.example.dminfo.model.Membro;
import com.example.dminfo.services.MembroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/membro")
public class MembroController {
    @Autowired
    private MembroService service;

    @GetMapping
    public ResponseEntity<Object> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    public ResponseEntity<Object> salvar(@RequestBody Membro membro) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(membro));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(@PathVariable Integer id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
