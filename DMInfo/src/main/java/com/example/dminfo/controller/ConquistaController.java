package com.example.dminfo.controller;

import com.example.dminfo.model.Conquista;
import com.example.dminfo.model.Erro;
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
    ResponseEntity<Object> listar() {
        return ResponseEntity.ok(conquistaService.listar());
    }

    @PostMapping
    ResponseEntity<Object> create(@RequestBody Conquista conquista) {
        if (conquista != null) {
            try {
                return ResponseEntity.status(HttpStatus.CREATED).body(conquistaService.salvar(conquista));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new Erro("Erro de Banco", e.getMessage()));
            }
        }

        return ResponseEntity.badRequest().body(new Erro("Erro de Objeto", "Objeto Membro inconsistente"));
    }

    @GetMapping(value="get-by-id/{id}")
    ResponseEntity<Object> read(@PathVariable Integer id) {
        Conquista conquista = conquistaService.getById(id);

        if (conquista != null) {
            return ResponseEntity.ok(conquista);
        }

        return ResponseEntity.badRequest().body(new Erro("Erro de Banco", "Usuario não encontrado"));
    }

    @PutMapping
    ResponseEntity<Object> update(@RequestBody Conquista conquista) {
        return create(conquista);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Object> delete(@PathVariable Integer id) {
        try {
            conquistaService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Erro("Erro de exclusão", e.getMessage()));
        }
    }
}
