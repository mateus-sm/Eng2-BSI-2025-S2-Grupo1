package com.example.dminfo.controller;

import com.example.dminfo.model.Erro;
import com.example.dminfo.model.Membro;
import com.example.dminfo.model.Usuario;
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
    public ResponseEntity<Object> create(@RequestBody Usuario usuario) {
        if (usuario != null) {
            try {
                return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(usuario));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new Erro("Erro de Banco", "Não foi possível salvar"));
            }
        }

        return ResponseEntity.badRequest().body(new Erro("Erro de Objeto", "Objeto Membro inconsistente"));
    }

    @GetMapping(value="get-by-id/{id}")
    ResponseEntity<Object> read(@PathVariable Integer id) {
        Membro membro = service.getById(id);

        if (membro == null) {
            return ResponseEntity.badRequest().body(new Erro("Erro de Banco", "Usuario não encontrado"));
        }

        return ResponseEntity.ok(membro);
    }

    @PutMapping
    public ResponseEntity<Object> update(@RequestBody Usuario usuario) {
        if (usuario != null) {
            try {
                return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(usuario));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new Erro("Erro de Banco", "Não foi possível atualizar"));
            }
        }

        return ResponseEntity.badRequest().body(new Erro("Erro de Objeto", "Objeto Membro inconsistente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        if (service.excluir(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().body(new Erro("Erro de busca", "Usuario não encontrado"));
        }
    }
}
