package com.example.dminfo.controller;


import com.example.dminfo.model.Doador;
import com.example.dminfo.services.DoadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doador")
public class DoadorController {

    @Autowired
    private DoadorService service;

    @GetMapping
    public ResponseEntity<Object> exibir() {
        return ResponseEntity.ok(service.exibir());
    }

    @PostMapping
    public ResponseEntity<Doador> salvar(@RequestBody Doador doador) {
        Doador salvo = service.salvar(doador);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(@PathVariable Integer id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}